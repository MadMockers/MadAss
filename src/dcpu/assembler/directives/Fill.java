package dcpu.assembler.directives;

import java.util.Arrays;

import dcpu.Tools;
import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Data;
import dcpu.assembler.entities.Literal;

public class Fill extends DirectiveHandler
{

	protected Fill()
	{
		super(2, "fill", "pad");
	}

	@Override
	public Literal resolveUnknown(String in)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CoreEntity[] handleDirective(Assembler a, ParserState state, String[] args)
	{
		int length = Tools.parseLiteral(args[0]);
		int value = Tools.parseLiteral(args[1]);
		
		int[] data = new int[length];
		Arrays.fill(data, value);
		return new CoreEntity[] {
			new Data(a, state.m_iLineNum, state.m_iProgramCounter, state.m_sRawLine, data)
		};
	}

}
