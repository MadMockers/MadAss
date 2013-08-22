package dcpu.assembler.directives;

import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Literal;

public class IfDef extends DirectiveHandler
{

	Define m_Define;
	
	int m_iDepth = 0;
	
	protected IfDef()
	{
		super(1, "ifdef");
		m_Define = (Define) DirectiveHandler.getDirectiveHandler("define");
		// TODO Auto-generated constructor stub
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
		if(!state.m_bParsing || m_Define.resolveUnknown(args[0]) == null)
		{
			m_iDepth++;
			state.m_bParsing = false;
		}
		return null;
	}

}
