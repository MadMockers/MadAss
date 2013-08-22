package dcpu.assembler.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dcpu.assembler.Assembler;
import dcpu.assembler.evaluate.Evaluator;

public class Label extends CoreEntity implements Literal
{
	
	Label m_Parent;

	String m_sName;
	
	boolean m_bUnknown;
	boolean m_bOptimized;
	
	List<Argument> m_vReferences = new ArrayList<Argument>();
	Map<String, Label> m_vChildren = new HashMap<String, Label>();
	
	// unknown
	public Label(Assembler a, Label parent, String name)
	{
		super(a, -1, -1, null);
		
		m_vReferences = new ArrayList<Argument>();
		
		m_bUnknown = true;
		m_sName = name;
		
		setParent(parent);
	}
	
	public Label(Assembler a, int lineN, String line, String name, int literal)
	{
		super(a, lineN, literal, line);
		
		m_vReferences = new ArrayList<Argument>();
		
		m_bUnknown = false;
		
		m_sName = name;
	}

	public Label(Assembler a, int lineN, String line, String name, Label parent, int literal)
	{
		this(a, lineN, line, name, literal);
		
		setParent(parent);
	}
	
	private void setParent(Label p)
	{
		m_Parent = p;
		
		if(m_Parent != null)
		{
			m_Parent.addChild(this);
		}
	}
	
	public void addChild(Label l)
	{
		m_vChildren.put(l.getName(), l);
	}
	
	public Label getChild(String name)
	{
		return m_vChildren.get(name);
	}
	
	public String getName()
	{
		return m_sName;
	}
	
	public void setInformation(int lineN, String line, int literal)
	{
		m_iLine = lineN;
		m_sRawLine = line;
		m_iPosition = literal;
		
		m_bUnknown = false;
	}
	
	public void addReference(Argument a)
	{
		m_vReferences.add(a);
	}
	
	public Argument[] getReferences()
	{
		return m_vReferences.toArray(new Argument[m_vReferences.size()]);
	}
	
	@Override
	public int getValue()
	{
		if(m_bUnknown)
		{
			// last ditch effort to see if the label is actually an expression:
			try
			{
				int value = Evaluator.evaluate(m_Assembler, getName());
				
				/* Don't set this - it changes the values that 'LiteralArgument's return, which breaks shit */
				//a.setLiteral(l);
				
				System.out.println("Evaluated '" + getName() + "' to " + value);
				return value;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new IllegalStateException("Label '" + getName() + "' is unknown!");
			}
		}
		return m_iPosition;
	}
	
	@Override
	public void setValue(int v)
	{
		m_iPosition = v;
	}
	
	public boolean isLocal()
	{
		return m_Parent != null;
	}
	
	public Label getParent()
	{
		return m_Parent;
	}
	
	public void setOptimized()
	{
		m_bOptimized = true;
	}
	
	public boolean isOptimized()
	{
		return m_bOptimized;
	}
	
	public boolean isUnknown()
	{
		return m_bUnknown;
	}
	
}
