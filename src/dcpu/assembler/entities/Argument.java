package dcpu.assembler.entities;

import dcpu.assembler.Assembler;

public class Argument extends Entity
{

	Operation m_Parent;
	String m_sText;
	int m_iCode;
	Literal m_Literal;
	boolean m_bFirst;
	
	public Argument(Assembler a, String arg, boolean first)
	{
		super(a);
		m_sText = arg;
		m_bFirst = first;
	}
	
	public Argument(Assembler a, String arg, boolean first, int code)
	{
		this(a, arg, first);
		m_iCode = code;
	}
	
	public void setParent(Operation p)
	{
		m_Parent = p;
	}
	
	public Operation getParent()
	{
		return m_Parent;
	}
	
	public void setLiteral(Literal l)
	{
		m_Literal = l;
	}
	
	public boolean hasLiteral()
	{
		return m_Literal != null;
	}
	
	public Literal getLiteral()
	{
		return m_Literal;
	}
	
	public int getCode()
	{
		return m_iCode;
	}

	public void setCode(int code)
	{
		m_iCode = code;
	}
	
}
