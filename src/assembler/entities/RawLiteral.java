package dcpu.assembler.entities;

import dcpu.assembler.Assembler;

public class RawLiteral extends Entity implements Literal
{

	int m_iValue;
	
	public RawLiteral(Assembler a, int value)
	{
		super(a);
		
		m_iValue = value;
	}
	
	@Override
	public int getValue()
	{
		return m_iValue;
	}
	
	@Override
	public void setValue(int v)
	{
		m_iValue = v;
	}

}
