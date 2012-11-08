package dcpu.assembler.entities;

import dcpu.assembler.Assembler;

public class LiteralArgument extends Argument
{

	public LiteralArgument(Assembler a, String arg, boolean first)
	{
		super(a, arg, first);
	}
	
	@Override
	public boolean hasLiteral()
	{
		if(!m_bFirst)
			return true;
		if(m_Literal instanceof RawLiteral)
		{
			int value = m_Literal.getValue();
	
			if(value >= -1 && value <= 30)
				return false;
			else
				return true;
		}
		else if(m_Literal instanceof Label)
		{
			return !((Label) m_Literal).isOptimized();
		}
		return true;
	}
	
	@Override
	public int getCode()
	{
		if(!m_bFirst)
			return 0x1f;
		
		boolean shortForm = true;
		int value = m_Literal.getValue();
		{
			if(m_Literal instanceof Label)
			{
				shortForm = ((Label) m_Literal).m_bOptimized;
			}
		}
		if(shortForm && value >= -1 && value <= 30)
			return 0x21 + value;
		else
			return 0x1f;
	}

}
