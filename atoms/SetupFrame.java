package atoms;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SetupFrame extends JFrame {
	final AtomSetup setup = new AtomSetup();

	public SetupFrame() {
		super("Atoms Setup");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			setLayout(new GridLayout(0, 4));

			for (final Field fld : AtomSetup.class.getDeclaredFields()) {
				add(new JLabel(fld.getName()));
				final JTextField tf = new JTextField("" + fld.get(setup));
				tf.getDocument().addDocumentListener(new DocumentListener() {
					public void insertUpdate(DocumentEvent e) { update(); }
					public void removeUpdate(DocumentEvent e) { update(); }
					public void changedUpdate(DocumentEvent e) { update(); }

					void update() {
						if ((fld.getType() + "").equals("double")) {
							try {
								fld.set(setup, Double.parseDouble(tf.getText()));
							} catch (Exception e) {
								//e.printStackTrace();
							}
						} else {
							try {
								fld.set(setup, Integer.parseInt(tf.getText()));
							} catch (Exception e) {
								//e.printStackTrace();
							}
						}
					}
				});
				add(tf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setSize(500, 600);
		JButton startB = new JButton("Start");
		startB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AtomFrame(setup);
			}
		});
		add(startB);

		setVisible(true);
	}
}
