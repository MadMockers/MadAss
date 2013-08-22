package dcpu.assembler.directives;

import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Literal;

public class EndIf extends DirectiveHandler
{

	IfDef m_IfDef;
	
	protected EndIf()
	{
		super(0, "endif");
		m_IfDef = (IfDef) DirectiveHandler.getDirectiveHandler("ifdef");
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
		if(m_IfDef.m_iDepth > 0)
		{
			m_IfDef.m_iDepth--;
			if(m_IfDef.m_iDepth == 0)
				state.m_bParsing = true;
		}
		return null;
	}

}
