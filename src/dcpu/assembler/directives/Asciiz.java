package dcpu.assembler.directives;

import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Data;
import dcpu.assembler.entities.Literal;

public class Asciiz extends DirectiveHandler
{

	protected Asciiz()
	{
		super(1, "asciiz");
	}

	@Override
	public Literal resolveUnknown(String in)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CoreEntity[] handleDirective(Assembler a, ParserState state,
			String[] args)
	{
		char[] ascii = args[0].toCharArray();
		
		int[] data = new int[ascii.length + 1];
		
		for(int i = 0;i < ascii.length;i++)
		{
			data[i] = ascii[i];
		}
		
		data[data.length - 1] = 0;
		
		return new CoreEntity[] { new Data(a, state.m_iLineNum, state.m_iProgramCounter, state.m_sRawLine, data) } ; 
	}

}
