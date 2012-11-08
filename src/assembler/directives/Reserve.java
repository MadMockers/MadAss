package dcpu.assembler.directives;

import dcpu.Tools;
import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Data;
import dcpu.assembler.entities.Literal;

public class Reserve extends DirectiveHandler
{

	protected Reserve()
	{
		super(1, "reserve", "emt", "empty");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Literal resolveUnknown(String in)
	{
		return null;
	}

	@Override
	public CoreEntity[] handleDirective(Assembler a, ParserState state,
			String[] args)
	{
		int size = Tools.parseLiteral(args[0]);
		
		return new CoreEntity[] {
			new Data(a, state.m_iLineNum, state.m_iProgramCounter, state.m_sRawLine, new int[size])	
		};
	}

}
