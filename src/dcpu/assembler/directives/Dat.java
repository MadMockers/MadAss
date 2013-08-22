package dcpu.assembler.directives;

import java.util.ArrayList;

import dcpu.Tools;
import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Data;
import dcpu.assembler.entities.Literal;
import dcpu.assembler.entities.OutputEntity;
import dcpu.assembler.entities.RawLiteral;
import dcpu.assembler.entities.ResolvableData;

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
		ArrayList<OutputEntity> data = new ArrayList<OutputEntity>();
		
		for(String arg : args)
		{

			int[] dat = Tools.convertDat(arg);
			if(dat.length == 0)
			{
				Literal l = a.parseLiteral(arg, true, true);
				if(l == null)
					throw new IllegalArgumentException("Unable to resolve literal '" + arg + "'");
				if(l instanceof RawLiteral)
				{
					data.add(new Data(a, state.m_iLineNum, state.m_iProgramCounter, state.m_sRawLine, new int[] {l.getValue()}));
				}
				else
				{
					data.add(new ResolvableData(a, state.m_iLineNum, state.m_iProgramCounter, state.m_sRawLine, l));
				}
			}
			else
			{
				data.add(new Data(a, state.m_iLineNum, state.m_iProgramCounter, state.m_sRawLine, dat));
			}
		}
		
		return data.toArray(new OutputEntity[data.size()]);
	}

}
