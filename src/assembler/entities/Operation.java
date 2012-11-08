package dcpu.assembler.entities;

import dcpu.assembler.Assembler;
import dcpu.assembler.OpCode;

public class Operation extends OutputEntity
{
	OpCode m_eOperation;
	Argument[] m_aArguments;

	public Operation(Assembler a, int lineN, String line, OpCode o, int pos,  String[] args)
	{
		super(a, lineN, pos, line);
		
		m_iPosition = pos;
		
		m_eOperation = o;
		m_aArguments = new Argument[o.getArgCount()];
		
		if(args.length != m_aArguments.length)
		{
			throw new IllegalArgumentException("Line " + lineN + 
					": Wrong argument count. Expected " + m_aArguments.length + 
					", got " + args.length);
		}
		
		// Start from top of array, since arguments are constructed in
		// 	reverse
		for(int i = args.length - 1;i >= 0;i--)
		{
			Argument arg = a.parseArgument(args[i], i == (args.length - 1));
			arg.setParent(this);
			m_aArguments[args.length - i - 1] = arg;
		}
	}
	
	public int getPosition()
	{
		return m_iPosition;
	}
	
	public void setPosition(int pos)
	{
		m_iPosition = pos;
	}
	
	public int[] getData()
	{
		int words = 1;
		
		{
			for(Argument a : m_aArguments)
			{
				if(a.hasLiteral())
					words++;
			}
		}
		
		int[] ret = new int[words];
		
		int idx = 1;
		
		OpCode op = m_eOperation;
		
		int opCode = op.getOpCode();
		
		int argsCode = 0;
		int argsShift = 10;
		
		Argument[] args = getArguments();
		for(Argument a : args)
		{
			argsCode |= a.getCode() << argsShift;
			if(a.hasLiteral())
			{
				Literal l = a.getLiteral();
				if(l instanceof Label)
				{
					Label lbl = (Label) l; 
					if(lbl.m_bUnknown)
						throw new IllegalStateException("Label '" + lbl.getName() + "' is unknown!");
				}
				ret[idx++] = a.getLiteral().getValue();
			}
			
			argsShift -= 5;
		}
		
		opCode |= argsCode;
		
		ret[0] = opCode;
		return ret;
	}
	
	public Argument[] getArguments()
	{
		return m_aArguments;
	}
	
	public String toString()
	{
		StringBuilder out = new StringBuilder(m_eOperation.name());
		for(int i = m_aArguments.length - 1;i >= 0;i--)
		{
			out.append(" ");
			out.append(m_aArguments[i].m_sText);
		}
		return out.toString();
	}
	
}
