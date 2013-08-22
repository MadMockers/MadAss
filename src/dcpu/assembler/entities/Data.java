package dcpu.assembler.entities;

import dcpu.assembler.Assembler;


public class Data extends OutputEntity
{

	int[] m_aData;
	
	public Data(Assembler a, int lineN, int pc, String line, int[] data)
	{
		super(a, lineN, pc, line);
		m_aData = data;
	}
	
	public int[] getData()
	{
		return m_aData;
	}
	
	public int getDataLength()
	{
		return m_aData.length;
	}

}
