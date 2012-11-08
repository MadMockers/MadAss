package dcpu.assembler;

import java.util.HashMap;

public enum OpCode
{

	SET(0x01, 2),
	ADD(0x02, 2),
	SUB(0x03, 2),
	MUL(0x04, 2),
	MLI(0x05, 2),
	DIV(0x06, 2),
	DVI(0x07, 2),
	MOD(0x08, 2),
	MDI(0x09, 2),
	AND(0x0A, 2),
	BOR(0x0B, 2),
	XOR(0x0C, 2),
	SHR(0x0D, 2),
	ASR(0x0E, 2),
	SHL(0x0F, 2),
	IFB(0x10, 2),
	IFC(0x11, 2),
	IFE(0x12, 2),
	IFN(0x13, 2),
	IFG(0x14, 2),
	IFA(0x15, 2),
	IFL(0x16, 2),
	IFU(0x17, 2),
	ADX(0x1A, 2),
	SBX(0x1B, 2),
	STI(0x1E, 2),
	STD(0x1F, 2),
	
	JSR(0x01 << 5, 1),
	INT(0x08 << 5, 1),
	IAG(0x09 << 5, 1),
	IAS(0x0A << 5, 1),
	RFI(0x0B << 5, 0),
	IAQ(0x0C << 5, 1),
	HWN(0x10 << 5, 1),
	HWQ(0x11 << 5, 1),
	HWI(0x12 << 5, 1),
	
	GRM(0x16 << 5, 1),
	DRM(0x17 << 5, 1),
	SRT(0x18 << 5, 1),
	
	DMP(0x1F << 5, 1);
	;
	
	private final int m_iOpCode;
	private final int m_iArgs;
	
	OpCode(int op, int args)
	{
		m_iOpCode = op;
		m_iArgs = args;
	}
	
	public int getOpCode()
	{
		return m_iOpCode;
	}
	
	public int getArgCount()
	{
		return m_iArgs;
	}
	
	private static HashMap<String, OpCode> m_vOperations;
	
	public static OpCode getOperation(String n)
	{
		return m_vOperations.get(n.toLowerCase());
	}
	
	static
	{
		m_vOperations = new HashMap<String, OpCode>();
		
		for(OpCode o : OpCode.values())
			m_vOperations.put(o.name().toLowerCase(), o);
	}
	
}
