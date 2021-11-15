import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Expression;

public class Interface extends JFrame {
	static public  Panel panel;
	private static  JTextField formule;
	public static String arbreFormat="";
	static Random ran = new Random();
	static int ch = ran.nextInt(4) + 1;
    static	int x = 500 ,  y = 20;
	public static JButton btnBitree ;
	public static JButton eva = new JButton("EVALUATE");

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				Interface frame = new Interface();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static  boolean isConnector(char character) {
		return character == '&' || character == '~' || character == '>' || character == '|' || character == '#';
	}

	public static Node construire (String  postfix) {
	     Stack<Node> pile = new Stack();
	     Node tree;
	     for (int i = 0; i<postfix.length(); i++) {
	         if (!isConnector(postfix.charAt(i))) { //cas de proposition
	             tree = new Node(postfix.charAt(i));
			 } else {

	             if(postfix.charAt(i)!='~') { //cas de connecteur binaire
	            	 tree = new Node(postfix.charAt(i));
		             tree.right = pile.pop();
		             tree.left = pile.pop();
	             } else {//cas de negation
	            	 tree = new Node(postfix.charAt(i));
	            	 tree.right = pile.pop();
	             }
			 }
			 pile.push(tree);
		 }
	     tree = pile.peek();
	     pile.pop();
	     return tree;
	 }

	public static void affichage(Node tree) {
		 if(tree!=null) {
			 arbreFormat = arbreFormat+tree.value;
			 System.out.print("(");
			 System.out.print(tree.value);
			 affichage(tree.left);
			 affichage(tree.right);
			 System.out.print(")");
		 }
	 }

    public static void drawNodes(Node tree, int x , int y) {
    	if(tree != null) {
    		Label l = new Label("       " + tree.value);
    		panel.add(l);

    		l.setBounds(x, y, 50, 50);
    		ch = ran.nextInt(4);
			switch (ch) {
				case 0 -> l.setBackground(new Color(225, 95, 65));
				case 1 -> l.setBackground(new Color(255, 234, 167));
				case 2 -> l.setBackground(new Color(163, 203, 56));
				case 3 -> l.setBackground(new Color(11, 232, 129));
				case 4 -> l.setBackground(new Color(248, 165, 194));
			}
    		if(tree.right!=null) {
    			if(tree.left==null) {
					drawNodes(tree.right,x,y+100);
    				panel.getGraphics().drawLine(x+20, y, x+20, y+100);
    			} else {
					drawNodes(tree.right,x+70,y+100);
    				panel.getGraphics().drawLine(x, y, x+70, y+100);
					drawNodes(tree.left,x-80,y+100);
    				panel.getGraphics().drawLine(x+10, y+50, x-40, y+100);
    			}
    		}
    	}
    }

	public Interface() {
		setTitle("LIATP");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(-2, 0, 1380, 720);
		JPanel contentPane = new JPanel();
		contentPane.setBackground(new Color(192, 57, 43));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		panel = new Panel();
		panel.setBackground(new Color(236, 240, 241));
		panel.setBounds(212, 11, 1143, 685);
		contentPane.add(panel);
		panel.setLayout(null);
		formule = new JTextField("p>(q&m)|~((s&g)|k)");
		formule.setFont(new Font("Tahoma", Font.BOLD, 18));
		formule.setBackground(new Color(242, 243, 244));
		formule.setBounds(10, 23, 192, 39);
		contentPane.add(formule);
		formule.setColumns(10);


		renderInputButton(contentPane, "~", 10, 82);
		renderInputButton(contentPane, "&", 119, 82);
		renderInputButton(contentPane, ">", 119, 132);
		renderInputButton(contentPane, "|", 10, 132);
		renderInputButton(contentPane, "#", 63, 194);

		JButton Go = new JButton("POSTFIX");
		Go.setBackground(new Color(210, 180, 222 ));
		Go.setBorderPainted(false);
		Go.setFocusPainted(false);
		Go.setFocusTraversalKeysEnabled(false);
		Go.setFocusable(false);
		Go.setFont(new Font("Tahoma", Font.PLAIN, 18));
		Go.setBounds(48, 260, 117, 39);
		contentPane.add(Go);
		Go.addActionListener(e -> {
			panel.removeAll();
			int  x =  10 , y = 10 ;
			String ex = formule.getText();
			String result;
			result = Postfix.infixToPostfix(ex);
			String[] strArray = result.split("");
			Label[] labels = new Label[strArray.length];
			int c  = 1;
			for(int i = 0 ; i < strArray.length; i++) {
			   labels[i] = new Label("     "+strArray[i]);
			   labels[i].setFont(new Font("Tahoma", Font.BOLD, 15));
			   if(x >= 1100 ) {
				   x = 10 ;
				   y = 80 ;
			   }
			   labels[i].setBounds(x, y, 50, 50);
			   if(c == 1) {
				  labels[i].setBackground(new Color(171, 235, 198 ));
				   c = 2;
			   } else if(c==2) {
				   labels[i].setBackground(new Color(249, 231, 159));
					c = 1 ;
			   }
			   panel.add(labels[i]);
			   x = x+55;
			}
		});

		JButton reset = new JButton("RESET");
		reset.setBackground(new Color(210, 180, 222));
		reset.setBorderPainted(false);
		reset.setFocusPainted(false);
		reset.setFocusTraversalKeysEnabled(false);
		reset.setFocusable(false);
		reset.setFont(new Font("Tahoma", Font.PLAIN, 18));
		reset.setBounds(48, 445, 117, 39);
		contentPane.add(reset);
		reset.addActionListener(e -> {
			formule.setText(formule.getText());
			panel.removeAll();
		});

		btnBitree = new JButton("BI-TREE");
		btnBitree.setBackground(new Color(210, 180, 222 ));
		btnBitree.setFocusPainted(false);
		btnBitree.setFocusTraversalKeysEnabled(false);
		btnBitree.setFocusable(false);
		btnBitree.setBorderPainted(false);
		btnBitree.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnBitree.setBounds(48, 318, 117, 39);
		contentPane.add(btnBitree);
		btnBitree.addActionListener(e -> {
			panel.removeAll();
			String ex = formule.getText();
			String result;
			result = Postfix.infixToPostfix(ex);
			System.out.println(result);
			Node Tree= construire(result);
			affichage(Tree);
			System.out.println("\n");
			drawNodes(Tree,x,y);
		});

		eva.setVisible(false);
		JButton evaluate = new JButton("EVAL EXP");
		evaluate.setFont(new Font("Tahoma", Font.PLAIN, 18));
		evaluate.setFocusable(false);
		evaluate.setFocusTraversalKeysEnabled(false);
		evaluate.setFocusPainted(false);
		evaluate.setBorderPainted(false);
		evaluate.setBackground(new Color(210, 180, 222));
		evaluate.setBounds(48, 378, 117, 39);
		contentPane.add(evaluate);
		evaluate.addActionListener(e -> {
			panel.removeAll();
			int  x =  10 , y = 10 ;
			String input = formule.getText();
			String extract = input.replaceAll("[^a-zA-Z]+", "");
			System.out.println(extract);
			String[] strArray = extract.split("");
			Label[] labels = new Label[strArray.length];
			int c  = 1;
			for(int i = 0; i < strArray.length; i++) {
				labels[i] = new Label("       "+strArray[i]);
				labels[i].setFont(new Font("Tahoma", Font.BOLD, 15));
				   if(x >= 950) {
					   x = 10 ;
					   y = 110 ;
				   }
				   labels[i].setBounds(x, y, 70, 70);
				   if(c == 1) {
					  labels[i].setBackground(new Color(171, 235, 198 ));
					   c = 2;
				   }
				   else if(c==2) {
					   labels[i].setBackground(new Color(249, 231, 159));
						c = 1;
				   }
				   panel.add(labels[i]);
				   x = x+200;
				}
			TextArea textArea = new TextArea();
			textArea.setBounds(10, 200, 1123, 460);
			panel.add(textArea);
			try {
				textArea.setFont(new Font("Tahoma", Font.BOLD, 22));
				textArea.setText(textArea.getText()+Main.postFixToTruthTable(Postfix.infixToPostfix(input))+"\n\n");
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			String z = "";

			StringBuilder sb = new StringBuilder();
			Set<Character> linkedHashSet = new LinkedHashSet<>();

			for (int i1 = 0; i1 < extract.length(); i1++){
				linkedHashSet.add(extract.charAt(i1));
			}

			for (Character c1 : linkedHashSet){
				sb.append(c1);
			}

			for (int j = 0; j <sb.length(); j++) {
				System.out.println("donner la valuer de "+sb.charAt(j)+": ");
				Scanner sc = new Scanner(System.in);
				char s = sc.next().charAt(0);
				for(int k  = 0 ; k<input.length();k++) {
					if(input.charAt(k) == sb.charAt(j)) {
					 z = input.replace(input.charAt(k),s);
					}
				}
				input = z;
				System.out.println(input);
			}

			Constant T = new Constant("T = 1");
			Constant F = new Constant("F = 0");
			Expression e1 = new Expression (input, T, F);
			System.out.println(e1.getExpressionString() + " = " + e1.calculate());
		});
	}

	private void renderInputButton(JPanel contentPane, String text, int x, int y) {
		JButton button = new JButton(text);
		button.setBackground(new Color(210, 180, 222 ));
		button.setFocusable(false);
		button.setFocusTraversalKeysEnabled(false);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setFont(new Font("Tahoma", Font.PLAIN, 23));
		button.setBounds(x, y, 83, 39);
		contentPane.add(button);
		button.addActionListener(e -> formule.setText(formule.getText() + text));
	}

}