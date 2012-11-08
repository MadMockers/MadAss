package dcpu.assembler.directives;

import java.util.HashMap;

import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Literal;

public class Define extends DirectiveHandler
{

	private HashMap<String, Literal> m_vDefines = new HashMap<String, Literal>();
	
	protected Define()
	{
		super(2, "define", "equ");
	}

	@Override
	public CoreEntity[] handleDirective(Assembler a, ParserState state, String[] args)
	{
		String name = args[0];
		String value = args[1];
		
		m_vDefines.put(name.toLowerCase(), a.parseLiteral(value));
		
		return null;
	}

	@Override
	public Literal resolveUnknown(String in)
	{
		return m_vDefines.get(in.toLowerCase());
	}

}