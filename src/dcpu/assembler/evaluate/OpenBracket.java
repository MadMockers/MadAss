package dcpu.assembler.evaluate;

import java.util.Deque;

public class OpenBracket extends Operator
{

	protected OpenBracket()
	{
		super("(", MAX_PRIORITY);
	}
	
	@Override
	public boolean execute(Deque<Operand> stack)
	{
		return false;
	}
	
	@Override
	public boolean mutuallyKillOperator(Operator op)
	{
		return op instanceof CloseBracket;
	}
	
	@Override
	public boolean hasPriorityOver(Operator op)
	{
		return op instanceof CloseBracket;
	}

}
