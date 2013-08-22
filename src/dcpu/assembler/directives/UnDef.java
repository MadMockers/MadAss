package dcpu.assembler.directives;

import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Literal;


public class UnDef extends DirectiveHandler
{

	Define m_Define;
	
	protected UnDef()
	{
		super(1, "undef");
		m_Define = (Define) DirectiveHandler.getDirectiveHandler("define");
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
		if(state.m_bParsing)
		{
			m_Define.undefine(args[0]);
		}
		return null;
	}
	
}
