package case2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Scatterplot extends javax.swing.JFrame {
	private List nodes = new ArrayList<>();
	Location depot;
	public static final int MODIFIER = 5;

	public Scatterplot(ArrayList<Location> al, Tour t) {
		super("Scatterplot");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

		addPoints(al);
		
		System.out.println("TOUR: ");
		for (Edge e : t.tour) { System.out.println(e.start.x + " " + e.start.y + " to " + e.end.x + " " + e.end.y); };
		System.out.println("END TOUR");

		JPanel panel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Color.BLACK);
				for (Iterator i = nodes.iterator(); i.hasNext();) {
					Point2D.Float pt = (Point2D.Float) i.next();
					Ellipse2D dot = new Ellipse2D.Float(MODIFIER * (pt.x - 1), MODIFIER * (pt.y - 1), 2*MODIFIER, 2*MODIFIER);
					g2d.fill(dot);
				}
				
				g2d.setColor(Color.RED);
				Ellipse2D dot = new Ellipse2D.Float(MODIFIER * (depot.x - 1), MODIFIER * (depot.y - 1), 2*MODIFIER, 2*MODIFIER);
				g2d.fill(dot);
				
				g2d.setColor(Color.BLUE);
				g2d.setStroke(new BasicStroke((float) (0.5 * MODIFIER)));
				
				for (Edge e : t.tour) {
					Line2D ln = new Line2D.Float(MODIFIER * e.start.x, MODIFIER* e.start.y,
							MODIFIER* e.end.x, MODIFIER * e.end.y);
					g2d.draw(ln);
				}
				
				g2d.dispose();
			};
		};

		setContentPane(panel);
		
		int xmin = (int) Double.POSITIVE_INFINITY;
		int ymin = (int) Double.POSITIVE_INFINITY;
		int xmax = (int) Double.NEGATIVE_INFINITY;
		int ymax = (int) Double.NEGATIVE_INFINITY;
		for(Location l : al) {
			xmin = Math.min(xmin, l.x);
			ymin = Math.min(ymin, l.y);
			xmax = Math.max(xmax, l.x);
			ymax = Math.max(ymax, l.y);
		}
		
		xmin *= MODIFIER;
		ymin *= MODIFIER;
		xmax *= MODIFIER;
		ymax *= MODIFIER;
		
		setBounds(xmin, ymin, xmax + xmin, ymax + ymin);
		setVisible(true);
	}

	void addPoints(ArrayList<Location> al) {
		for (Location l : al) {
			if (!l.isDepot){
				nodes.add(new Point2D.Float(l.x, l.y));
			} else {
				depot = l;
			}
		}
	}
}
