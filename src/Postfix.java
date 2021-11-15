import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.PropositionalParser;

class Postfix {

	public static void main(String[] args) throws Exception {
		String exp = "(A =>B) | S";
		final FormulaFactory f = new FormulaFactory();
		final PropositionalParser p = new PropositionalParser(f);
		final Formula formula = p.parse(exp);
		final Formula cnf = formula.cnf();
		System.out.println(exp);
		System.out.println("Koniunkcyjna postać normalna: ");
		System.out.println(cnf);
	}

}
