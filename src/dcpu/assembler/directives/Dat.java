package dcpu.assembler.directives;

import java.util.ArrayList;

import dcpu.Tools;
import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Data;
import dcpu.assembler.entities.Literal;

public class Dat extends DirectiveHandler
{

	protected Dat()
	{
		super(-1, "dat");
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
		ArrayList<Data> data = new ArrayList<Data>();
		
		for(String arg : args)
			data.add(new Data(a, state.m_iLineNum, state.m_iProgramCounter, state.m_sRawLine, Tools.convertDat(arg)));
		
		return data.toArray(new Data[data.size()]);
	}

}
