package dcpu.assembler.evaluate;

import java.util.Deque;

public class CloseBracket extends Operator
{

	protected CloseBracket()
	{
		super(")", 0);
	}

	@Override
	public boolean execute(Deque<Operand> stack)
	{
		return false;
	}

}
