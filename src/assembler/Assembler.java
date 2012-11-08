package dcpu.assembler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import dcpu.Tools;
import dcpu.assembler.directives.DirectiveHandler;
import dcpu.assembler.entities.Argument;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Directive;
import dcpu.assembler.entities.Label;
import dcpu.assembler.entities.Literal;
import dcpu.assembler.entities.LiteralArgument;
import dcpu.assembler.entities.Operation;
import dcpu.assembler.entities.OutputEntity;
import dcpu.assembler.entities.RawLiteral;

public class Assembler
{
	
	private File m_fileInput;

	private String m_sOutputName;
	
	private ArrayList<String> m_vRawLines = new ArrayList<String>();
	private ArrayList<CoreEntity> m_vEntities = new ArrayList<CoreEntity>();
	
	private HashMap<String, Label> m_vUnknownLabels = new HashMap<String, Label>();
	private HashMap<String, Label> m_vLabelsByName = new HashMap<String, Label>();
	private ArrayList<Label> m_vLabels = new ArrayList<Label>();
	
	private Label m_LastLabel;
	
	public Assembler(String inFile, String outFile)
	{
		m_fileInput = new File(inFile);
		if(!m_fileInput.exists())
		{
			throw new IllegalArgumentException("File '" + inFile + "' does not exist!");
		}
		
		m_sOutputName = outFile;
	}
	
	public void assemble()
	{
		firstPass();
		optimizeLabels();
		secondPass();
	}
	
	private void firstPass()
	{		
		ParserState state = new ParserState();
		
		for(state.m_iLineNum = 0;state.m_iLineNum < m_vRawLines.size();state.m_iLineNum++)
		{
			try
			{
				state.m_sRawLine = m_vRawLines.get(state.m_iLineNum);
				String line = state.m_sRawLine;
				
				// strip out comments
				{
					int idx = line.indexOf(';');
					if(idx != -1)
						line = line.substring(0, idx);
				}
				line = line.trim().toLowerCase();
				
				if(line.length() == 0 || line.charAt(0) == '#')
				{
					m_vEntities.add(new CoreEntity(this, state.m_iLineNum, state.m_iProgramCounter, state.m_sRawLine));
					continue;
				}
				
				// special handling (aka hax)
				{
					String tLine = line.toLowerCase();
					if(tLine.startsWith("push"))
					{
						line = "SET " + line;
					}
					else if(tLine.startsWith("pop"))
					{
						String[] split = line.split(" ");
						line = "SET " + split[1] + " " + split[0];
					}
					if(tLine.equals("ret"))
						line = "set pc, pop";
					if(tLine.startsWith("dat"))
						line = "." + line;
				}
				
				String[] split = Tools.split(line);
				
				String ent = split[0].toLowerCase();
				
	
				if(state.m_bParsing)
				{
					// operations
					{
						OpCode o = OpCode.getOperation(ent);
						if(o != null)
						{
							String[] args = new String[split.length - 1];
							System.arraycopy(split, 1, args, 0, args.length);
							
							Operation op = new Operation(this, state.m_iLineNum, state.m_sRawLine, o, state.m_iProgramCounter, args);
							m_vEntities.add(op);
							
							state.m_iProgramCounter++;
							
							for(Argument a : op.getArguments())
							{
								// Literal l = a.getLiteral();
								if(a.hasLiteral())
									state.m_iProgramCounter++;
							}
							
							continue;	// If it's an operation, then it's not going to be anything else
						}
					}
					
					// labels
					{
						// allow support for ':label' and 'label:'
						boolean isLabel = false;
						if(ent.charAt(0) == ':')
						{
							ent = ent.substring(1);
							isLabel = true;
						}
						else if(ent.charAt(ent.length() - 1) == ':')
						{
							ent = ent.substring(0, ent.length() - 1);
							isLabel = true;
						}
						if(isLabel)
						{
							Label parent = null;
							boolean local = false;
							if(ent.charAt(0) == '.')
							{
								local = true;
								if(m_LastLabel == null)
								{
									throw new IllegalArgumentException("Local label used with no context!");
								}
								parent = m_LastLabel;
							}
							Label l = m_vUnknownLabels.remove(ent);
							
							if(l == null)
							{
								l = new Label(this, state.m_iLineNum, state.m_sRawLine, ent, parent, state.m_iProgramCounter);
							}
							else
							{
								l.setInformation(state.m_iLineNum, state.m_sRawLine, state.m_iProgramCounter);
							}
							
							m_vEntities.add(l);
							m_vLabels.add(l);
							
							if(!local)
							{
								m_LastLabel = l;
								m_vLabelsByName.put(l.getName().toLowerCase(), l);
							}
							
							continue;
						}
					}
				}
					
				// directive
				{
					if(ent.charAt(0) == '.')
					{
						ent = ent.substring(1);
						
						DirectiveHandler dh = DirectiveHandler.getDirectiveHandler(ent);
						
						if(dh == null)
						{
							throw new IllegalStateException("Directive '." + ent + "' doesn't exist!");
						}
						
						String params = line.substring(line.indexOf(' ') + 1);
						
						Directive dir = new Directive(this, state.m_iLineNum, state.m_iProgramCounter, state.m_sRawLine, dh, ent, params);
						m_vEntities.add(dir);
						
						CoreEntity[] ents = dir.handleDirective(state);
						
						if(ents != null && state.m_bParsing)
						{
							for(CoreEntity e : ents)
							{
								if(e instanceof OutputEntity)
								{
									state.m_iProgramCounter += ((OutputEntity) e).getData().length;
								}
								m_vEntities.add(e);
							}
						}
						continue;
					}
				}
				
				throw new IllegalStateException("I don't recognize this!");
			}
			catch(Exception e)
			{
				System.err.println("Error on line " + state.m_iLineNum + ": " + state.m_sRawLine);
				e.printStackTrace();
			}
		}
	}
	
	public void optimizeLabels()
	{
		ArrayList<Label> toOptimize = null;
		
		int count = 0;
		
		do
		{
			toOptimize = new ArrayList<Label>();
			for(Label l : m_vLabels)
			{
				if(!l.isOptimized() && l.getValue() < 31)
				{
					toOptimize.add(l);
				}
			}
			
			for(Label opt : toOptimize)
			{
				opt.setOptimized();
				
				Argument[] uses = opt.getReferences();
				
				count += uses.length;
				
				if(uses.length == 0)
					continue;
				
				for(Label l : m_vLabels)
				{
					if(l.getName().equals("gpf_handler"))
					{
						int a = 0;
					}
					int offset = 0;

					int lastUseIdx = 0;
					
					Argument[] compUses = l.getReferences();
					
					for(int i = 0;i < uses.length;i++)
					{
						if(!(uses[i] instanceof LiteralArgument) || !uses[i].isFirst())
							continue;
						
						int use = uses[i].getParent().getPosition();
						
						if(use < l.getValue())
							offset++;
						
						for(int j = lastUseIdx;true;j++)
						{
							if(j >= compUses.length)
							{
								lastUseIdx = j;
								break;
							}
							int compUse = compUses[j].getParent().getPosition();
							if(compUse > use)
							{
								lastUseIdx = j;
								break;
							}
							
							compUses[j].getParent().setPosition(compUse - i);
						}
					}
					for(int j = lastUseIdx;j < compUses.length;j++)
					{
						int compUse = compUses[j].getParent().getPosition();
						
						compUses[j].getParent().setPosition(compUse - uses.length);
					}
					l.setValue(l.getValue() - offset);
				}
			}
		}
		while(toOptimize.size() != 0);
		
		System.out.println("Optimization used " + count + " less words!");
	}
	
	private void secondPass()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		int pc = 0;
		for(CoreEntity e : m_vEntities)
		{
//			System.out.println(pc + ": " + e.getLine());
			if(e instanceof OutputEntity)
			{
				int[] code = ((OutputEntity) e).getData();
				pc += code.length;
				try
				{
					for(int c : code)
						dos.writeShort(c);
				}
				catch(IOException ee)
				{
					ee.printStackTrace();
				}
			}
		}
		
		{
			File output = new File(m_sOutputName + ".bin");
			try
			{
				OutputStream os = new FileOutputStream(output);
				os.write(baos.toByteArray());
				os.flush();
				os.close();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void loadFile()
	{
		loadFile(m_fileInput);
	}
	
	private void loadFile(File f)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(f));
			
			while(br.ready())
			{
				String line = br.readLine();
				m_vRawLines.add(line);
				
				line = line.trim();
				if(line.length() == 0)
					continue;
				
				if(line.charAt(0) == '#')
				{
					String[] split = Tools.split(line);
					if(split[0].equalsIgnoreCase("#include"))
					{
						if(split.length != 2)
						{
							throw new IllegalArgumentException("Not enough parameters for '" + split[0] + "'."); // TODO tell line number
						}
						if(split[1].charAt(0) == '"')
						{
							split[1] = split[1].substring(1, split[1].length() - 1);
						}
						File includeFile = new File(split[1]);
						if(!includeFile.exists())
						{
							throw new IllegalArgumentException("Include file '" + split[1] + "' does not exist!");
						}
						loadFile(includeFile);
					}
				}
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Literal parseLiteral(String in)
	{
		in = in.toLowerCase();
		
		try
		{
			int literal = Tools.parseLiteral(in);
			return new RawLiteral(this, literal);
		}
		catch(NumberFormatException e) {}
		
		// check directives
		{
			Literal l = null;;
			Iterator<DirectiveHandler> it = DirectiveHandler.iterator();
			while(it.hasNext())
			{
				l = it.next().resolveUnknown(in);
				if(l != null)
					return l;
			}
		}
		
		// assume literal is a label
		{	
			boolean isLocal = in.charAt(0) == '.';
			
			if(isLocal && m_LastLabel == null)
				throw new IllegalStateException("Attempted to reference a local label with no context!");
			
			Label l = isLocal ? m_LastLabel.getChild(in) : m_vLabelsByName.get(in);
			if(l == null)
			{
				l = m_vUnknownLabels.get(in);
				if(l == null)
				{
					l = new Label(this, isLocal ? m_LastLabel : null, in);
					m_vUnknownLabels.put(in, l);
				}
			}
			return l;
		}
	}
	
	public Argument parseArgument(String in, boolean firstArgument)
	{
		String arg = in.toLowerCase();
		
		Argument argEnt = new Argument(this, in, firstArgument);
		Literal literal = null;
		
		boolean dereference = false;
		
		if(in.charAt(0) == '[')
		{
			if(in.charAt(in.length() - 1) != ']')
			{
				throw new IllegalArgumentException("Argument '" + in + "' has no closing bracket!");
			}
			arg = arg.substring(1, arg.length() - 1);
			dereference = true;
		}
		
		if(dereference)
		{
			if(arg.contains("+") || arg.contains("-"))
			{
				String[] split = null;
				boolean negative = false;
				if(arg.contains("+"))
					split = arg.split("\\+");
				if(arg.contains("-"))
					split = arg.split("\\-");
				
				literal = parseLiteral(split[1]);
				if(negative)
				{
					if(literal instanceof RawLiteral)
						literal.setValue(-literal.getValue());
					else
						throw new IllegalArgumentException("Cannot evaluate the subtraction of a non raw literal!");
				}
				
				int registerId = getGeneralPurposeRegisterId(split[0]);
				if(registerId != -1)
				{
					// [register + literal]
					argEnt.setCode(registerId + 0x10);
				}
				else
				{
					if(split[0].equalsIgnoreCase("sp"))
					{
						argEnt.setCode(0x1a);
					}
				}
			}
			else
			{
				int registerId = getGeneralPurposeRegisterId(arg);
				if(registerId != -1)
				{
					// [register]
					argEnt.setCode(registerId + 0x08);
				}
				else if(arg.equals("sp"))
				{
					// [sp]
					argEnt.setCode(0x19);
				}
				else
				{
					// [literal]
					argEnt.setCode(0x1e);
					literal = parseLiteral(arg);
				}
			}
		}
		else
		{ // not dereference (not using [ ])
			int registerId = getGeneralPurposeRegisterId(arg);
			if(registerId != -1)
			{
				argEnt.setCode(registerId);
			}
			else if(arg.equals("push") || arg.equals("pop"))
			{
				argEnt.setCode(0x18);
			}
			else if(arg.equals("peek"))
			{
				argEnt.setCode(0x19);
			}
			else if(arg.startsWith("pick"))
			{
				String[] split = arg.split(" ");
				argEnt.setCode(0x1a);
				literal = parseLiteral(split[1]);
			}
			else if(arg.equals("sp"))
			{
				argEnt.setCode(0x1b);
			}
			else if(arg.equals("pc"))
			{
				argEnt.setCode(0x1c);
			}
			else if(arg.equals("ex"))
			{
				argEnt.setCode(0x1d);
			}
			else
			{
				argEnt = new LiteralArgument(this, in, firstArgument);
				//argEnt.setCode(0x1f);
				literal = parseLiteral(arg);
			}
		}
		
		if(literal instanceof Label)
		{
			((Label) literal).addReference(argEnt);
		}
		
		argEnt.setLiteral(literal);
		
		return argEnt;
	}
	
	public void outputDebugData()
	{
		File file = new File(m_sOutputName + ".dbg");
		
		try
		{
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
			
			HashMap<Integer, ArrayList<Integer>> linesByPC = new HashMap<Integer, ArrayList<Integer>>();
			
			{
				int pc = 0;
				for(CoreEntity e : m_vEntities)
				{
					ArrayList<Integer> lines = linesByPC.get(pc);
					
					if(lines == null)
					{
						lines = new ArrayList<Integer>();
						linesByPC.put(pc, lines);
					}
					
					if(e instanceof OutputEntity)
						pc += ((OutputEntity) e).getData().length;
					
					lines.add(e.getLineNumber());
				}
			}
			
//			{
//				HashMap<Integer, Label> labels = new HashMap<Integer, Label>();
//				for(Label l : m_vLabelsByName.values())
//					labels.put(l.getValue(), l);
//				Integer[] pcs = labels.keySet().toArray(new Integer[0]);
//				Arrays.sort(pcs);
//				for(int pc : pcs)
//				{
//					Label l = labels.get(pc);
//					if(linesByPC.containsKey(pc))
//					{
//						ArrayList<Integer> lines = linesByPC.get(pc);
//						
//						boolean out = false;
//						for(int lineNum : lines)
//						{
//							String line = m_vRawLines.get(lineNum).trim();
//							
//							if(line.length() == 0)
//								continue;
//							
//							if(line.trim().charAt(0) == ':')
//							{
//								out = true;
//								System.out.println(String.format("%d\t%64s\t%s", pc, l.getName(), line));
//							}
//						}
//						if(!out)
//							System.out.println("LABEL " + l.getName() + ": Could not find!");
//					}
//				}
//			}
			
			dos.writeInt(m_vLabelsByName.size());
			for(Label l : m_vLabelsByName.values())
			{
				dos.writeUTF(l.getName());
				dos.writeInt(l.getValue());
			}
			
			dos.writeInt(linesByPC.size());
			for(int pc : linesByPC.keySet())
			{
				dos.writeInt(pc);
				
				ArrayList<Integer> lines = linesByPC.get(pc);
				
				dos.writeInt(lines.size());
				
				for(int line : lines)
					dos.writeInt(line);
			}
			
			dos.writeInt(m_vRawLines.size());
			for(String line : m_vRawLines)
				dos.writeUTF(line);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static HashMap<String, Integer> g_vRegisters = new HashMap<String, Integer>();
	
	private static int getGeneralPurposeRegisterId(String in)
	{
		Integer ret = g_vRegisters.get(in.trim().toLowerCase());
		if(ret == null)
			return -1;
		return ret;
	}
	
	static
	{
		g_vRegisters.put("a", 0);
		g_vRegisters.put("b", 1);
		g_vRegisters.put("c", 2);
		g_vRegisters.put("x", 3);
		g_vRegisters.put("y", 4);
		g_vRegisters.put("z", 5);
		g_vRegisters.put("i", 6);
		g_vRegisters.put("j", 7);
	}
	
	public static void main(String[] args)
	{
		Assembler a = new Assembler(args[0], args[1]);
		
		a.loadFile();
		a.assemble();
		a.outputDebugData();
	}
	
	public class ParserState
	{
		public boolean m_bParsing = true;
		public int m_iLineNum = 0;
		public String m_sRawLine = "";
		public int m_iProgramCounter = 0;
	}
	
}
