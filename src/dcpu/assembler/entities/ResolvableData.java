package dcpu.assembler.entities;

import dcpu.assembler.Assembler;

public class ResolvableData extends OutputEntity
{

	Literal m_Literal;
	
	public ResolvableData(Assembler a, int lineN, int pc, String line, Literal l)
	{
		super(a, lineN, pc, line);
		m_Literal = l;
	}

	@Override
	public int[] getData()
	{
		return new int[] { m_Literal.getValue() };
	}

	@Override
	public int getDataLength()
	{
		return 1;
	}

}
