package dcpu.assembler.entities;

import dcpu.assembler.Assembler;

public class CoreEntity extends Entity
{

	int m_iLine;
	String m_sRawLine;
	int m_iPosition;
	
	public CoreEntity(Assembler a, int lineN, int pc,  String line)
	{
		super(a);
		
		m_iLine = lineN;
		m_sRawLine = line;
		m_iPosition = pc;
	}
	
	public int getLineNumber()
	{
		return m_iLine;
	}
	
	public int getPosition()
	{
		return m_iPosition;
	}
	
	public String getLine()
	{
		return m_sRawLine;
	}

}
