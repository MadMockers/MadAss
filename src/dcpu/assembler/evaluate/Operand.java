package dcpu.assembler.evaluate;

public class Operand extends Entity
{
	int m_iValue;

	Operand(String v)
	{
		m_iValue = Integer.parseInt(v);
	}
	
	Operand(int v)
	{
		m_iValue = v;
	}
}
