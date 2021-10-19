package smt;

public class SMT_test {
	public static void main(String[] args) {
		String[] addresses = new String[] {"1", "2", "4","5", "6"};
		String target = "3";
		SMT s = new SMT();
		s.hello();
		s.proveExist(addresses, target);
		
	}
}
