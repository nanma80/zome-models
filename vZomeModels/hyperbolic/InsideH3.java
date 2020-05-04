import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import java.util.Arrays;
import java.util.Comparator;



class Point3D{
	public double x,y,z;
	public Point3D(double X, double Y, double Z) {
		x = X; y = Y; z = Z;
	}
	public void rotate3D (Point3D axis, double theta)	{
		
		double oldx = x;
		double oldy = y;
		double oldz = z;
		double norm = Math.sqrt(axis.x *axis.x +axis.y *axis.y +axis.z *axis.z);
		double ux =  axis.x/norm;
		double uy =  axis.y/norm;
		double uz =  axis.z/norm;

		double newx = oldx * (Math.cos(theta) + ux*ux*(1-Math.cos(theta))) 
				+ oldy * (ux*uy*(1-Math.cos(theta))-uz*Math.sin(theta)) 
				+ oldz * (ux*uz*(1-Math.cos(theta))+uy*Math.sin(theta));
		double newy = oldx* (ux*uy*(1-Math.cos(theta))+uz*Math.sin(theta))
				+ oldy * (Math.cos(theta) + uy*uy*(1-Math.cos(theta))) 
				+ oldz * (uy*uz*(1-Math.cos(theta))-ux*Math.sin(theta));
		double newz = oldx * (ux*uz*(1-Math.cos(theta))-uy * Math.sin(theta)) 
				+ oldy* (uy*uz*(1-Math.cos(theta))+ux * Math.sin(theta))  
				+ oldz * (Math.cos(theta)+uz*uz*(1-Math.cos(theta)));
		x = newx;
		y = newy;
		z = newz;
	}
}

class Point4D{
	public double x,y,z,t,x2d, y2d, pointsize,dist;
	public int curvature;
//	int[] nb = {0,0,0,0,0, 0,0,0,0,0, 0,0};
//	int nnb;
	
	public Point4D(double T, double X, double Y, double Z, int CURVATURE) {
		x = X; y = Y; z = Z; t = T;
		x2d = 0; y2d=0; pointsize = 1; dist = 1;
		curvature = CURVATURE;
//		for (int i = 0; i<12; i++) nb[i] = 0;
//		nnb = 0;
//	xscr = 0; yscr = 0;
		normalize();
		update2d();
	}
	
	public void normalize() {
		if (curvature < 0){
			double norm = Math.sqrt(t*t - x*x -y*y - z*z);
			x /= norm; y /= norm; z /= norm; t /= norm;
		} else if (curvature > 0){
			double norm = Math.sqrt(t*t + x*x + y*y + z*z);
			x /= norm; y /= norm; z /= norm; t /= norm;
		} else {
			x /= t; y /= t; z /= t;
		}
	}

	public double acosh (double z) {
		return Math.log(z + Math.sqrt((z+1)*(z-1)));						
	}
	
	public double asinh (double z) {
		return Math.log(z + Math.sqrt(z*z+1));						
	}
	
	public void update2d () {
		// looking from (t,x,y,z)=(1,0,0,0) to the +z direction
		// assuming this Point4D has been normalized to hyperboloid
		//double zz=z;
		
		if (curvature < 0) {
			x2d = x/(z+1e-10); y2d = y/(z+1e-10);
			dist = acosh(t+1e-10);
			pointsize = 2/dist;
		} else if (curvature > 0) {
			x2d = x/(z+1e-10); y2d = y/(z+1e-10);
			
			if (z>0 ) {
				dist = Math.acos(t+1e-10);
			} else {
				dist = Math.PI*2 - Math.acos(t+1e-10);
			}
			if (Math.acos(Math.abs(t))!=0.)		pointsize = 2/Math.acos(Math.abs(t));
			else pointsize=10.;
			
		} else {
			x2d = x/(z+1e-10); y2d = y/(z+1e-10);
			dist = Math.sqrt(x*x + y*y + z*z);
			pointsize = 2/dist;
		}
		

		if (pointsize>8) pointsize = 8;
		//if (pointsize<1/2) pointsize = 0;
	}
	
	public void rotateXYZ(double theta, double ux, double uy	) {
		double norm = Math.sqrt(ux*ux + uy*uy);
		ux /= norm; uy /= norm;
		double newx = x * (Math.cos(theta) + ux*ux*(1-Math.cos(theta))) + y* (ux*uy*(1-Math.cos(theta))) + z * (uy*Math.sin(theta));
		double newy = y * (Math.cos(theta) + uy*uy*(1-Math.cos(theta))) + x* (ux*uy*(1-Math.cos(theta))) + z * (-ux*Math.sin(theta));
		double newz = x * (-uy * Math.sin(theta)) + y* (ux * Math.sin(theta)) + z * (Math.cos(theta));
		x = newx;
		y = newy;
		z = newz;
		//normalize();
		update2d();
	}
	
	public void rotateTZ(double theta) {
		if (curvature < 0) {
			double newt = t * Math.cosh(theta) + z * Math.sinh(theta);
			double newz = t * Math.sinh(theta) + z * Math.cosh(theta);
			t = newt;
			z = newz;
		} else if (curvature > 0) {
			double newt = t * Math.cos(theta) - z * Math.sin(theta);
			double newz = t * Math.sin(theta) + z * Math.cos(theta);
			t = newt;
			z = newz;
		} else {
			z += theta;
		}
		//normalize();
		update2d();
	}
}

class Edge {
	public int p1, p2;
	public Edge (int P1, int P2) {
		p1 = P1; p2 = P2;
	}
}

public class InsideH3 extends Applet
implements MouseMotionListener, ComponentListener, ItemListener {
	private static final long serialVersionUID = 1L;
	int width, height;
	int mx, my;  // the mouse coordinates
	Point center;
	Point4D[] p4d;
	int np4d = 0;
	Edge[] edges;
	int ne = 0;
	int nedgepoints = 0;
	//double edgethreshold=1.01;

	int curvature = 1; 
	
	CheckboxGroup CBmodels;  
	Checkbox CB534; 
	Checkbox CB435;
	Checkbox CB535;
	Checkbox CB353;
	Checkbox CB433;
	Checkbox CB333;
	Checkbox CB334;
	Checkbox CB343;
	Checkbox CB335;
	Checkbox CB533;
	Checkbox CB434;
	Checkbox CB443;
	Checkbox CB633;
	Checkbox CB733;
	Checkbox CB734;
	Checkbox CB735;
	Checkbox CBi33;
	Checkbox CBi34;
	Checkbox CBi35;
	Checkbox CB634;
	Checkbox CB635;
	Checkbox CBClif;
	Checkbox CBgap;
	Checkbox CBB333;
	Checkbox CBB343;
	Checkbox CBB434;
	
	
	     
	double tau = (Math.sqrt(5)+1)/2;
	double ttau = tau * tau;
	double tau2 = ttau;
	double tau12 = Math.pow(tau, 0.5);
	double tau32 = Math.pow(tau, 1.5);
	double tau52 = Math.pow(tau, 2.5);
	double tau3 = ttau * tau;
	double tau72 = Math.pow(tau, 3.5);
	double tau4 = ttau * ttau;
	double tau92 = Math.pow(tau, 4.5);
	double tau5 = tau4 * tau;
	double tau6 = tau4 * ttau;
	double tau112 = tau92*tau;

	double phi = (Math.sqrt(5)+1)/2;
	double phi2 = phi * phi;
	double phi3 = phi2 * phi;
	double phi4 = phi3 * phi;
	double phi5 = phi4 * phi;
	double phi6 = phi5 * phi;
	double phi12 = Math.pow(phi, 0.5);
	double phi32 = Math.pow(phi, 1.5);
	double phi52 = Math.pow(phi, 2.5);
	double phi72 = Math.pow(phi, 3.5);
	double phi92 = Math.pow(phi, 4.5);
	double phi112 = phi92*phi;
	

	double coshedge = 0;
	double cosh2edge = 0;
	int ngon;
	
	Label label344, label345, label354, label355,label444,label445,label734,label735,labeli34,labeli35;
	
	Panel CBs;
	Panel intro;
	Panel CBmisc;
	
	Image backbuffer;
	Graphics backg;
	
	
	double scaleScreen;
	double scalePoint;
	double rotationspeed = 0.01;
	public int[][] perms4 = {{0, 1, 2, 3}, {0, 1, 3, 2}, {0, 2, 1, 3}, {0, 2, 3, 1}, {0, 3, 1, 
		  2}, {0, 3, 2, 1}, {1, 0, 2, 3}, {1, 0, 3, 2}, {1, 2, 0, 3}, {1, 2, 
			  3, 0}, {1, 3, 0, 2}, {1, 3, 2, 0}, {2, 0, 1, 3}, {2, 0, 3, 1}, {2, 
			  1, 0, 3}, {2, 1, 3, 0}, {2, 3, 0, 1}, {2, 3, 1, 0}, {3, 0, 1, 
			  2}, {3, 0, 2, 1}, {3, 1, 0, 2}, {3, 1, 2, 0}, {3, 2, 0, 1}, {3, 2, 
			  1, 0}};
	
	public int[][] perms42 = {{0, 1, 2, 3}, {0, 1, 3, 2}, {0, 2, 1, 3}, {0, 2, 3, 1}, {0, 3, 1, 2}, {0, 3, 2, 1}, 
							  //{1, 0, 2, 3}, {1, 0, 3, 2}, {1, 2, 0, 3}, {1, 2, 3, 0}, {1, 3, 0, 2}, {1, 3, 2, 0},
							  
							  {2, 0, 1, 3}, {2, 0, 3, 1}, 
							  //{2, 1, 0, 3}, {2, 1, 3, 0}, 
							  
							  {2, 3, 0, 1}, 
							  //{2, 3, 1, 0},
							  
							  {3, 0, 1, 2}, 
							  //{3, 1, 0, 2}, 
							  {3, 0, 2, 1}, 
							  //{3, 1, 2, 0}, 
							  {3, 2, 0, 1} 
							  //{3, 2, 1, 0}
							  };
	
	public int[][] perma4 = {{0, 1, 2, 3}, {0, 2, 3, 1}, {0, 3, 1, 2}, {1, 0, 3, 2}, {1, 2, 0, 
		  3}, {1, 3, 2, 0}, {2, 0, 1, 3}, {2, 1, 3, 0}, {2, 3, 0, 1}, {3, 0, 
			  2, 1}, {3, 1, 0, 2}, {3, 2, 1, 0}};
	
	public int[][] cycle4 = {{0, 1, 2, 3}, {1,2,3,0}, {2,3,0,1}, {3,0,1,2}};
	
	public int[][] choose42 = {{0, 1, 2, 3}, {1,2,3,0}, {2,3,0,1}, {3,0,1,2}, {0,2,1,3}, {2,0,3,1}};
	

	public void init() {
		width = getSize().width;
		height = getSize().height;

		
		mx = width/2;
		my = height/2;
		//setLayout(null);
		
		center = new Point((width-200)/2,height/2);
		scaleScreen = (double)((width+height)/8);
		scalePoint = 10.;
		
		p4d = new Point4D[1600];
		edges = new Edge[1600];
		
		
		CBmodels = new CheckboxGroup(); 
		
		
		CB534 = new Checkbox("5,3,4", CBmodels, false);  
		CB435 = new Checkbox("4,3,5", CBmodels, false);
		CB535 = new Checkbox("5,3,5", CBmodels, false);
		CB353 = new Checkbox("3,5,3", CBmodels, false);
		CB433 = new Checkbox("4,3,3", CBmodels, false);
		CB333 = new Checkbox("3,3,3", CBmodels, false);
		CB334 = new Checkbox("3,3,4", CBmodels, false);
		CB343 = new Checkbox("3,4,3", CBmodels, false);
		CB335 = new Checkbox("3,3,5", CBmodels, false);
		CB533 = new Checkbox("5,3,3", CBmodels, false);
		CB434 = new Checkbox("4,3,4", CBmodels, false);
		CB443 = new Checkbox("4,4,3", CBmodels, false);
		CB633 = new Checkbox("6,3,3", CBmodels, false);
		CB733 = new Checkbox("7,3,3", CBmodels, false);
		CB734 = new Checkbox("7,3,4", CBmodels, false);
		CB735 = new Checkbox("7,3,5", CBmodels, false);
		CBi33 = new Checkbox("inf,3,3", CBmodels, false);
		CBi34 = new Checkbox("inf,3,4", CBmodels, false);
		CBi35 = new Checkbox("inf,3,5", CBmodels, false);
		CB634 = new Checkbox("6,3,4", CBmodels, false);
		CB635 = new Checkbox("6,3,5", CBmodels, false);
		CBClif = new Checkbox("Clifford", CBmodels, false);
		CBgap = new Checkbox("Grand Antiprism", CBmodels, false);
		CBB333 = new Checkbox("Bitrunc. 3,3,3", CBmodels, false);
		CBB343 = new Checkbox("Bitrunc. 3,4,3", CBmodels, false);
		CBB434 = new Checkbox("Bitrunc. 4,3,4", CBmodels, false);
		
		CB535.setState(true);
		
		label344 = new Label("");
		label345 = new Label("");
		label354 = new Label("");
		label355 = new Label("");
		label444 = new Label("");
		label445 = new Label("");
		label734 = new Label("");
		label735 = new Label("");
		labeli34 = new Label("");
		labeli35 = new Label("");
		
		Color hyper = new Color(220,220,255);
		Color euclid = new Color(255,230,200);
		Color spher = new Color(255,210,255);
		Color NA = Color.lightGray;
		
		CB534.setBackground(hyper);
		CB435.setBackground(hyper);
		CB535.setBackground(hyper);
		CB353.setBackground(hyper);
		CB443.setBackground(hyper);
		CB633.setBackground(hyper);
		CB733.setBackground(hyper);
		CB734.setBackground(hyper);
		CB735.setBackground(hyper);
		CBi33.setBackground(hyper);
		CBi34.setBackground(hyper);
		CBi35.setBackground(hyper);
		CB634.setBackground(hyper);
		CB635.setBackground(hyper);
		CBClif.setBackground(spher);
		CBgap.setBackground(spher);
		CBB333.setBackground(spher);
		CBB343.setBackground(spher);
		CB433.setBackground(spher);
		CB333.setBackground(spher);
		CB334.setBackground(spher);
		CB343.setBackground(spher);
		CB335.setBackground(spher);
		CB533.setBackground(spher);
		CB434.setBackground(euclid);
		CBB434.setBackground(euclid);
		label344.setBackground(NA);
		label345.setBackground(NA);
		label354.setBackground(NA);
		label355.setBackground(NA);
		label445.setBackground(NA);
		label444.setBackground(NA);
		label734.setBackground(NA);
		label735.setBackground(NA);
		labeli34.setBackground(NA);
		labeli35.setBackground(NA);
		
		
		
		//CB333.setBounds(CBwidth1,CBheight1,CBwidth,CBheight);
		//CB433.setBounds(CBwidth1,CBheight1,CBwidth,CBheight);
		

		
		//CB534.setForeground(Color.white);
		//CB435.setForeground(Color.white);
		
		CB435.addItemListener(this);
		CB534.addItemListener(this);
		CB535.addItemListener(this);
		CB353.addItemListener(this);
		CB433.addItemListener(this);
		CB333.addItemListener(this);
		CB334.addItemListener(this);
		CB343.addItemListener(this);
		CB335.addItemListener(this);
		CB533.addItemListener(this);
		CB434.addItemListener(this);
		CB443.addItemListener(this);
		CB633.addItemListener(this);
		CB733.addItemListener(this);
		CB734.addItemListener(this);
		CB735.addItemListener(this);
		CBi33.addItemListener(this);
		CBi34.addItemListener(this);
		CBi35.addItemListener(this);
		CB634.addItemListener(this);
		CB635.addItemListener(this);
		CBClif.addItemListener(this);
		CBgap.addItemListener(this);
		CBB333.addItemListener(this);
		CBB343.addItemListener(this);
		CBB434.addItemListener(this);
		
		this.setLayout (null);
		
		intro = new Panel();
		intro.setLayout(new GridLayout(2,1));
		intro.setBackground(Color.lightGray);
		intro.setBounds(width - 150, height - 40, 150, 40);
		this.add(intro);
		Label introlabel1 = new Label("Drag to turn", Label.CENTER); 
		Label introlabel2 = new Label("Shift + drag to move.", Label.CENTER);
		intro.add(introlabel1);
		intro.add(introlabel2);
		
		CBs = new Panel();
		CBs.setLayout(new GridLayout(11,4,0,0));
		CBs.setBackground(new Color(220,250,250));
		CBs.setBounds(width - 250, 0, 250, 11*25);
		this.add(CBs);
		
		CBmisc = new Panel();
		CBmisc.setLayout(new GridLayout(7,1,0,0));
		CBmisc.setBackground(new Color(220,250,250));
		CBmisc.setBounds(width - 125, 11*25+1, 125, 7*25);
		this.add(CBmisc);
		
		CBs.add (new Label("Regular",Label.CENTER));
		CBs.add (new Label("Polytopes",Label.CENTER));
		CBs.add (new Label(""));
		CBs.add (new Label(""));
		CBs.add (new Label("cells/edge",Label.CENTER));
		CBs.add (new Label("3",Label.CENTER));
		CBs.add (new Label("4",Label.CENTER));
		CBs.add (new Label("5",Label.CENTER));
		
		CBs.add (new Label("tetra",Label.CENTER));
		CBs.add(CB333);
		CBs.add(CB334);
		CBs.add(CB335);
		
		CBs.add (new Label("cube",Label.CENTER));
		CBs.add(CB433);
		CBs.add(CB434);
		CBs.add(CB435);
		
		CBs.add (new Label("dodeca",Label.CENTER));
		CBs.add(CB533);
		CBs.add(CB534);
		CBs.add(CB535);
		
		CBs.add (new Label("{6,3}",Label.CENTER));
		CBs.add(CB633);
		CBs.add(CB634);
		CBs.add(CB635);
		
		CBs.add (new Label("{7,3}",Label.CENTER));
		CBs.add(CB733);
		CBs.add(CB734);
		CBs.add(CB735);
		//CBs.add (label734);
		//CBs.add (label735);
		
		CBs.add (new Label("{inf,3}",Label.CENTER));
		CBs.add(CBi33);
		CBs.add(CBi34);
		CBs.add(CBi35);
		//CBs.add (labeli34);
		//CBs.add (labeli35);
		
		CBs.add (new Label("octa",Label.CENTER));
		CBs.add (CB343);
		CBs.add (label344);
		CBs.add (label345);
		
		CBs.add (new Label("{4,4}",Label.CENTER));
		CBs.add(CB443);
		CBs.add (label444);
		CBs.add (label445);
		
		CBs.add (new Label("icosa",Label.CENTER));
		CBs.add(CB353);
		CBs.add (label354);
		CBs.add (label355);

		CBmisc.add (new Label("Bitruncated",Label.CENTER));
		CBmisc.add(CBB333);
		CBmisc.add(CBB343);
		CBmisc.add(CBB434);
		
		CBmisc.add (new Label("Torus-ish",Label.CENTER));
		CBmisc.add(CBClif);
		CBmisc.add(CBgap);
		
/*		
		CBs.add (new Label("cells:",Label.CENTER));
		
		CBs.add (new Label("cubes",Label.CENTER));
		CBs.add (new Label("dodecahedra",Label.CENTER));
		CBs.add (new Label("{6,3}",Label.CENTER));
		CBs.add (new Label("misc.",Label.CENTER));
		CBs.add (new Label("bitruncated",Label.CENTER));
		CBs.add (new Label("torus",Label.CENTER));
		
		CBs.add (new Label("3 cells/edge",Label.CENTER));
		CBs.add(CB333);
		CBs.add(CB433);
		CBs.add(CB533);
		CBs.add(CB633);
		CBs.add(CB343);		
		CBs.add(CBB333);
		CBs.add(CBClif);
		
		CBs.add (new Label("4 cells/edge",Label.CENTER));
		CBs.add(CB334);
		CBs.add(CB434);
		CBs.add(CB534);
		CBs.add(CB634);
		CBs.add(CB443);
		//CBs.add(label344);
		//CBs.add(label444);
		//CBs.add(label354);
		CBs.add(CBB343);
		CBs.add(CBgap);
		
		CBs.add (new Label("5 cells/edge",Label.CENTER));
		CBs.add(CB335);
		CBs.add(CB435);
		CBs.add(CB535);
		CBs.add(CB635);
		CBs.add(CB353);
		//CBs.add(label345);
		CBs.add(CBB434);
		CBs.add(label445);
		//CBs.add(label355);
		//CBs.add(CBClif);
		//CBs.add(CBgap);
		
		//CBs.add(CB333);
		//CBs.add(CB435);
		//setLayout(new GridLayout(3,5,10,30));
*/

		/*
		add(CB435);
		add(CB534);
		add(CB535);
		add(CB353);
		add(CB433);
		add(CB333);
		add(CB334);
		add(CB343);
		add(CB335);
		add(CB533);
		add(CB434);
		*/
		
		initpuzzle ();

		//seed534();
		//grow();
				
		
		backbuffer = createImage( width, height );
		backg = backbuffer.getGraphics();
		
		backg.setColor(Color.black);
		backg.fillRect(0, 0, width, height);
		//Graphics2D g2d = (Graphics2D)backg;
		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		addMouseMotionListener( this );
		this.addComponentListener(this);
		
		drawPuzzle( backg );
		repaint();
	}

	/*
	
	public void seed534 ( ) {
		coshedge = tau;
		ngon = 5;
		
		double x1 = 1;
		double x2 = 0;
		double x3 = 0;
		
		double k = Math.sqrt((coshedge*coshedge - 1)/(x1*x1+x2*x2+x3*x3));
		
		p4d[0] = new Point4D(1,0,0,0);
		p4d[1] = new Point4D(coshedge,k*x1,k*x2,k*x3);
		p4d[2] = new Point4D(coshedge,k*x2,k*x3,k*x1);
		p4d[3] = new Point4D(coshedge,k*x3,k*x1,k*x2);
		
		x1 = -1;
		
		p4d[4] = new Point4D(coshedge,k*x1,k*x2,k*x3);
		p4d[5] = new Point4D(coshedge,k*x2,k*x3,k*x1);
		p4d[6] = new Point4D(coshedge,k*x3,k*x1,k*x2);
		
		np4d = 7;
		cosh2edge = coshdistance(1,2);
		
		
		for (int i=0; i<6; i++ ) {
			p4d[0].nb[p4d[0].nnb]= i+1;
			p4d[0].nnb++;
			edges[ne] = new Edge(0,i+1);
			ne ++;
			p4d[i+1].nb[p4d[i+1].nnb] = 0;
			p4d[i+1].nnb++;
		}
		
		for (int i = 0; i< np4d; i++) {
			p4d[i].rotateTZ(0.5);
			p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			p4d[i].rotateTZ(-0.5);
			p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			p4d[i].rotateTZ(-0.1);
			p4d[i].rotateXYZ(0.3, -1, 0.7);
		}
	
	}
	
	public double coshdistance (int i, int j) {
		return p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y - p4d[i].z * p4d[j].z;
	}
	
	public void grow () {
		int pointerp4d = 0;
		//int maxdegree = 6; 		if (ngon == 5) maxdegree = 12;
		
		for (int k = 0; k< 40; k++)
		{
			if (p4d[pointerp4d].nnb < 2) {
				pointerp4d ++;
				if (pointerp4d == np4d) return;
				continue;
			}
			for (int i = 0; i<p4d[pointerp4d].nnb-1; i++)
				for (int j = i+1; j< p4d[pointerp4d].nnb; j++) {			
					//System.out.println("i = "+i+"  j = "+j);
					int nbi = p4d[pointerp4d].nb[i];
					int nbj = p4d[pointerp4d].nb[j];
					//System.out.println("nbi = "+nbi+"  nbj = "+nbj);
					if (coshdistance(nbi,nbj) < 1.01 * cosh2edge) {
						if (ngon == 5) {
							double newt = p4d[nbi].t + tau * p4d[nbj].t - tau * p4d[pointerp4d].t;
							double newx = p4d[nbi].x + tau * p4d[nbj].x - tau * p4d[pointerp4d].x;
							double newy = p4d[nbi].y + tau * p4d[nbj].y - tau * p4d[pointerp4d].y;
							double newz = p4d[nbi].z + tau * p4d[nbj].z - tau * p4d[pointerp4d].z;
							
							int nj=0;
							int ni=0;
							
							boolean ignorej = false;
							for (int jj = 0; jj <p4d[nbj].nnb; jj++) {
								int neighbor = p4d[nbj].nb[jj];
								if ((newx - p4d[neighbor].x) * (newx - p4d[neighbor].x) + (newy - p4d[neighbor].y)* (newy - p4d[neighbor].y)
										+ (newz - p4d[neighbor].z)* (newz - p4d[neighbor].z)< 0.1) {
									//System.out.println("jj = "+jj);
									ignorej = true;
									nj = neighbor;
									break;
								}
							}
							if (! ignorej) {
								p4d[np4d] = new Point4D (newt, newx, newy, newz);
								Double dObj = new Double(p4d[np4d].x);
								boolean b2 = dObj.isNaN();
								if (b2) {
									System.out.println("Error! np4d = "+ np4d + " nbi = "+nbi + " nbj = "+nbj+ " pointerp4d = "+pointerp4d);
									return;
								}
								p4d[np4d].nb[p4d[np4d].nnb] = nbj; p4d[np4d].nnb ++;
								p4d[nbj].nb[p4d[nbj].nnb] = np4d; p4d[nbj].nnb++;
								edges[ne] = new Edge(np4d,nbj); ne++;
								nj = np4d;
								if (np4d==103)
									System.out.println("np4d = "+ np4d + " nbi = "+nbi + " nbj = "+nbj+ " pointerp4d = "+pointerp4d+ " k = "+k);
								np4d ++;
							}
							
							int substi = nbj;
							nbj = nbi;
							nbi = substi;
							
							newt = p4d[nbi].t + tau * p4d[nbj].t - tau * p4d[pointerp4d].t;
							newx = p4d[nbi].x + tau * p4d[nbj].x - tau * p4d[pointerp4d].x;
							newy = p4d[nbi].y + tau * p4d[nbj].y - tau * p4d[pointerp4d].y;
							newz = p4d[nbi].z + tau * p4d[nbj].z - tau * p4d[pointerp4d].z;

							
							boolean ignorei = false;
							for (int jj = 0; jj <p4d[nbj].nnb; jj++) {
								int neighbor = p4d[nbj].nb[jj];
								if ((newx - p4d[neighbor].x) * (newx - p4d[neighbor].x) + (newy - p4d[neighbor].y)* (newy - p4d[neighbor].y)
										+ (newz - p4d[neighbor].z)* (newz - p4d[neighbor].z)< 0.1) {
									ignorei = true;
									ni = neighbor;
									break;
								}
							}
							if (! ignorei) {
								p4d[np4d] = new Point4D (newt, newx, newy, newz);
								p4d[np4d].nb[p4d[np4d].nnb] = nbj; p4d[np4d].nnb ++;
								p4d[nbj].nb[p4d[nbj].nnb] = np4d; p4d[nbj].nnb++; 
								edges[ne] = new Edge(np4d,nbj); ne++;
								ni = np4d;
								np4d ++;
								
							}
							
							//System.out.println("np4d = "+np4d+ "  ni = "+ni+ "  nj = "+ nj);
							
							if (!ignorej || !ignorei) {
								p4d[nj].nb[p4d[nj].nnb] = ni; 
								p4d[nj].nnb++;
								p4d[ni].nb[p4d[ni].nnb] = nj; 
								p4d[ni].nnb++;
								edges[ne] = new Edge(nj,ni); ne++;
							} else {
								boolean ignoreedge = false;
								for (int jj = 0; jj <p4d[nj].nnb; jj++) {
									if (p4d[nj].nb[jj] == ni) {
										ignoreedge = true; break;
									}
								}
								if (!ignoreedge) {
									p4d[nj].nb[p4d[nj].nnb] = ni; 
									p4d[nj].nnb++;
									p4d[ni].nb[p4d[ni].nnb] = nj; 
									p4d[ni].nnb++;
									edges[ne] = new Edge(nj,ni); ne++;
								}
							}
							
						} else if (ngon ==4) {
							
						}
					}
					
					
				}
			
		
		pointerp4d ++;
		if (pointerp4d == np4d) return;
		}
		
	}
	
	*/
	
	public void initpuzzle () {
		if (CB435.getState()) {
			curvature = -1;
			init435();
		} else if (CB534.getState()) {
			curvature = -1;
			init534();
		} else if (CB535.getState()) {
			curvature = -1;
			init535();
		} else if (CB353.getState()) {
			curvature = -1;
			init353();
		} else if (CB433.getState()) {
			curvature = 1;
			init433();
		} else if (CB333.getState()) {
			curvature = 1;
			init333();
		} else if (CB334.getState()) {
			curvature = 1;
			init334();
		} else if (CB343.getState()) {
			curvature = 1;
			init343();
		} else if (CB533.getState()) {
			curvature = 1;
			init533();
		} else if (CB335.getState()) {
			curvature = 1;
			init335();
		} else if (CB434.getState()) {
			curvature = 0;
			init434();
		} else if (CB443.getState()) {
			curvature = -1;
			init443();
		} else if (CB634.getState()) {
			curvature = -1;
			init634();
		} else if (CB633.getState()) {
			curvature = -1;
			init633();
		} else if (CB635.getState()) {
			curvature = -1;
			init635();
		} else if (CBClif.getState()) {
			curvature = 1;
			initClif();
		} else if (CBgap.getState()) {
			curvature = 1;
			initgap();
		} else if (CBB333.getState()) {
			curvature = 1;
			initB333();
		} else if (CBB343.getState()) {
			curvature = 1;
			initB343();
		} else if (CBB434.getState()) {
			curvature = 0;
			initB434();
		} else if (CB733.getState()) {
			curvature = -1;
			init733();
		} else if (CBi33.getState()) {
			curvature = -1;
			initi33();
		} else if (CB734.getState()) {
			curvature = -1;
			init734();
		} else if (CB735.getState()) {
			curvature = -1;
			init735();
		} else if (CBi34.getState()) {
			curvature = -1;
			initi34();
		} else if (CBi35.getState()) {
			curvature = -1;
			initi35();
		} 
		//System.out.println("The number of vertices = "+np4d + "  The number of edges = "+ ne + " coshedge = "+coshedge);
	}

	public void initi35 ( ) {

		curvature = -1;
		coshedge = 4. + Math.sqrt(5.);
		double sinhedge = Math.sqrt(coshedge*coshedge - 1);
		
		
		np4d = 0;
		p4d[np4d++] = new Point4D(1,0,0,0, curvature);
		
		p4d[np4d++] = new Point4D(coshedge,sinhedge/Math.sqrt(ttau+1)*tau, -sinhedge/Math.sqrt(ttau+1),0,curvature);
		p4d[np4d++] = new Point4D(coshedge,sinhedge/Math.sqrt(ttau+1)*tau, sinhedge/Math.sqrt(ttau+1),0,curvature);
		p4d[np4d++] = new Point4D(coshedge,0,sinhedge/Math.sqrt(ttau+1)*tau,sinhedge/Math.sqrt(ttau+1), curvature);
		p4d[np4d++] = new Point4D(coshedge,sinhedge/Math.sqrt(ttau+1),0,sinhedge/Math.sqrt(ttau+1)*tau, curvature);
				

		int p0, p1, p2,p00, p10, p20, p1k, p2k, p0k, k0;
		double ka,kb,kz;
		int irange = 4;
		
		{
			p0 = 0; p10 = 2; p1k = 0; p20 = 2; p2k = 1; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}

		{
			p00 = 2; p0k=0; p10 = 5; p1k = 0; p20 = 8; p2k = 2; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}

		{
			p00 = 5; p0k=0; p10 = 11; p1k = 0; p20 = irange*6-1; p2k = 0; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}
		
		{
			p00 = 8; p0k=0; p10 = 14; p1k = 0; p20 = irange*6+2; p2k = 1; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}
		


		for (int k = 1; k<=4; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=4; k++) {
			int axisindex = 1;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=4; k++) {
			int axisindex = 77;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}

		
		
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==2) System.out.println(" "+ i + " " + j + " " +index);
					edges[ne++] = new Edge(i,j);
				}
			}
		}

		
		
		//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.3, -1, 0.7);
		}
		
		
	}

	public void initi34 ( ) {

		curvature = -1;
		coshedge = 3.;
		double sinhedge = Math.sqrt(coshedge*coshedge - 1);
		
		
		np4d = 0;
		p4d[np4d++] = new Point4D(1,0,0,0, curvature);
		
		p4d[np4d++] = new Point4D(coshedge,-sinhedge,0,0, curvature);
		p4d[np4d++] = new Point4D(coshedge,sinhedge,0,0, curvature);
		p4d[np4d++] = new Point4D(coshedge,0,sinhedge,0, curvature);
		p4d[np4d++] = new Point4D(coshedge,0,0,sinhedge, curvature);
		

		int p0, p1, p2,p00, p10, p20, p1k, p2k, p0k, k0;
		double ka,kb,kz;
		int irange = 4;
		
		{
			p0 = 0; p10 = 2; p1k = 0; p20 = 2; p2k = 1; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}

		{
			p00 = 2; p0k=0; p10 = 5; p1k = 0; p20 = 8; p2k = 2; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}

		{
			p00 = 5; p0k=0; p10 = 11; p1k = 0; p20 = irange*6-1; p2k = 0; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}
		
		{
			p00 = 8; p0k=0; p10 = 14; p1k = 0; p20 = irange*6+2; p2k = 1; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}
		
		

		for (int k = 1; k<=3; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/4);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}

		
		for (int k = 1; k<=3; k++) {
			int axisindex = 3;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/4);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 0., 1.);
		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-acosh(coshedge));

		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(acosh(coshedge));
				//p4d[i].x *= -1;
				//p4d[i].y *= -1;
				//newp4d.z *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		for (int k = 1; k<=1; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/4);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		/*
		for (int k = 1; k<=1; k++) {
			int axisindex = 3;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		

		double rotangle = Math.atan(p4d[2].y/p4d[2].z);
		//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(rotangle, 1., 0.);
		//System.out.println(" " + p4d[2].t+" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);

		
		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(-acosh(coshedge));
				//p4d[i].x *= -1;
				//p4d[i].y *= -1;
				newp4d.z *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=1; k++) {
			int axisindex = 1;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		*/
		
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==2) System.out.println(" "+ i + " " + j + " " +index);
					edges[ne++] = new Edge(i,j);
				}
			}
		}

		
		
		//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
		}
		
		
	}
	
	public void init735 ( ) {

		curvature = -1;
		coshedge = -1 + (5.+Math.sqrt(5.))* Math.cos(Math.PI/7.)* Math.cos(Math.PI/7.);
		double sinhedge = Math.sqrt(coshedge*coshedge - 1);
		//double sqrt2 = Math.sqrt(2.);
		
		np4d = 0;
		p4d[np4d++] = new Point4D(1,0,0,0, curvature);

		p4d[np4d++] = new Point4D(coshedge,sinhedge/Math.sqrt(ttau+1)*tau, -sinhedge/Math.sqrt(ttau+1),0,curvature);
		p4d[np4d++] = new Point4D(coshedge,sinhedge/Math.sqrt(ttau+1)*tau, sinhedge/Math.sqrt(ttau+1),0,curvature);
		p4d[np4d++] = new Point4D(coshedge,0,sinhedge/Math.sqrt(ttau+1)*tau,sinhedge/Math.sqrt(ttau+1), curvature);
		p4d[np4d++] = new Point4D(coshedge,sinhedge/Math.sqrt(ttau+1),0,sinhedge/Math.sqrt(ttau+1)*tau, curvature);
				

		int p0, p1, p2,p00, p10, p20, p1k, p2k, p0k, k0;
		double ka,kb,kz;
		
		{
			p0 = 0; p10 = 2; p1k = 0; p20 = 2; p2k = 1; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		{
			p00 = 2; p0k=0; p10 = 5; p1k = 0; p20 = 8; p2k = 2; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
				
		{
			p00 = 5; p0k=0; p10 = 11; p1k = 0; p20 = 17; p2k = 0; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		{
			p00 = 8; p0k=0; p10 = 14; p1k = 0; p20 = 20; p2k = 1; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		{
			p00 = 11; p0k=0; p10 = 14; p1k = 0; p20 = 29; p2k = 0; k0=0;
			//for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {  p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		

		for (int k = 1; k<=4; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=4; k++) {
			int axisindex = 1;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=4; k++) {
			int axisindex = 62;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==2) System.out.println(" "+ i + " " + j + " " +index);
					edges[ne++] = new Edge(i,j);
				}
			}
		}

		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 0., 1.);
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.3, -1, 0.7);
		}
	}

	public void init734 ( ) {

		curvature = -1;
		coshedge = 1 + 2* Math.sin(3*Math.PI/14.);
		double sinhedge = Math.sqrt(coshedge*coshedge - 1);
		//double sqrt2 = Math.sqrt(2.);
		
		np4d = 0;
		p4d[np4d++] = new Point4D(1,0,0,0, curvature);
		
		p4d[np4d++] = new Point4D(coshedge,-sinhedge,0,0, curvature);
		p4d[np4d++] = new Point4D(coshedge,sinhedge,0,0, curvature);
		p4d[np4d++] = new Point4D(coshedge,0,sinhedge,0, curvature);
		p4d[np4d++] = new Point4D(coshedge,0,0,sinhedge, curvature);
		
		

		int p0, p1, p2,p00, p10, p20, p1k, p2k, p0k, k0;
		double ka,kb,kz;
		
		{
			p0 = 0; p10 = 2; p1k = 0; p20 = 2; p2k = 1; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		{
			p00 = 2; p0k=0; p10 = 5; p1k = 0; p20 = 8; p2k = 2; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
				
		{
			p00 = 5; p0k=0; p10 = 11; p1k = 0; p20 = 17; p2k = 0; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		{
			p00 = 8; p0k=0; p10 = 14; p1k = 0; p20 = 20; p2k = 1; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		{
			p00 = 11; p0k=0; p10 = 14; p1k = 0; p20 = 29; p2k = 0; k0=0;
			//for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {  p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		

		for (int k = 1; k<=3; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/4);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=3; k++) {
			int axisindex = 3;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/4);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		
		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 0., 1.);
		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-acosh(coshedge));

		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(acosh(coshedge));
				//p4d[i].x *= -1;
				//p4d[i].y *= -1;
				//newp4d.z *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		for (int k = 1; k<=2; k++) {
			int axisindex = 3;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/4);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==2) System.out.println(" "+ i + " " + j + " " +index);
					edges[ne++] = new Edge(i,j);
				}
			}
		}

		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 0., 1.);
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
		}
	}

	public void initi33 ( ) {

		curvature = -1;
		coshedge = 2.;
		double sinhedge = Math.sqrt(coshedge*coshedge - 1);
		double sqrt2 = Math.sqrt(2.);
		
		np4d = 0;
		p4d[np4d++] = new Point4D(1,0,0,0, curvature);
		
		p4d[np4d++] = new Point4D(coshedge,sinhedge,0,0, curvature);
		p4d[np4d++] = new Point4D(coshedge,-sinhedge/3.,-2*sqrt2/3*sinhedge,0, curvature);		
		p4d[np4d++] = new Point4D(coshedge,-sinhedge/3.,sqrt2/3*sinhedge,Math.sqrt(6)/3*sinhedge, curvature);
		p4d[np4d++] = new Point4D(coshedge,-sinhedge/3.,sqrt2/3*sinhedge,-Math.sqrt(6)/3*sinhedge, curvature);
		

		int p0, p1, p2,p00, p10, p20, p1k, p2k, p0k, k0;
		double ka,kb,kz;
		int irange = 4;
		
		{
			p0 = 0; p10 = 2; p1k = 0; p20 = 2; p2k = 1; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}

		{
			p00 = 2; p0k=0; p10 = 5; p1k = 0; p20 = 8; p2k = 2; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}

		{
			p00 = 5; p0k=0; p10 = 11; p1k = 0; p20 = irange*6-1; p2k = 0; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}
		
		{
			p00 = 8; p0k=0; p10 = 14; p1k = 0; p20 = irange*6+2; p2k = 1; k0=0;
			for (int i=2; i<=irange; i++) {
				ka = -(i*i-1); kb = i*(i+1)/2; kz = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
				ka = -(i*i-1); kz = i*(i+1)/2; kb = i*(i-1)/2;
				for (int k = k0; k < 3; k++) {
					p0=p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;		
					p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);
				}
			}
		}
		/*
		{
			p00 = 8; p0k=0; p10 = 14; p1k = 0; p20 = 20; p2k = 1; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		{
			p00 = 11; p0k=0; p10 = 14; p1k = 0; p20 = 29; p2k = 0; k0=0;
			//for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {  p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		*/
		

		for (int k = 1; k<=2; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}

		
		for (int k = 1; k<=1; k++) {
			int axisindex = 3;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 0., 1.);
		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-acosh(coshedge));

		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(acosh(coshedge));
				//p4d[i].x *= -1;
				//p4d[i].y *= -1;
				newp4d.z *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		for (int k = 1; k<=2; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=1; k++) {
			int axisindex = 3;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		

		double rotangle = Math.atan(p4d[2].y/p4d[2].z);
		//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(rotangle, 1., 0.);
		//System.out.println(" " + p4d[2].t+" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);

		
		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(-acosh(coshedge));
				//p4d[i].x *= -1;
				//p4d[i].y *= -1;
				newp4d.z *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=1; k++) {
			int axisindex = 1;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==2) System.out.println(" "+ i + " " + j + " " +index);
					edges[ne++] = new Edge(i,j);
				}
			}
		}

		
		
		//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		
		{
		//	for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
		//	for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 0.,1.);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-2.2);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI, 0., 1.);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.4);
		}
		
		
	}
	
	public void init733 ( ) {

		curvature = -1;
		coshedge = -1 + 3* Math.cos(Math.PI/7.) * Math.cos(Math.PI/7.);
		double sinhedge = Math.sqrt(coshedge*coshedge - 1);
		double sqrt2 = Math.sqrt(2.);
		
		np4d = 0;
		p4d[np4d++] = new Point4D(1,0,0,0, curvature);
		
		p4d[np4d++] = new Point4D(coshedge,sinhedge,0,0, curvature);
		p4d[np4d++] = new Point4D(coshedge,-sinhedge/3.,2*sqrt2/3*sinhedge,0, curvature);		
		p4d[np4d++] = new Point4D(coshedge,-sinhedge/3.,-sqrt2/3*sinhedge,Math.sqrt(6)/3*sinhedge, curvature);
		p4d[np4d++] = new Point4D(coshedge,-sinhedge/3.,-sqrt2/3*sinhedge,-Math.sqrt(6)/3*sinhedge, curvature);
		

		int p0, p1, p2,p00, p10, p20, p1k, p2k, p0k, k0;
		double ka,kb,kz;
		
		{
			p0 = 0; p10 = 2; p1k = 0; p20 = 2; p2k = 1; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = 0; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		{
			p00 = 2; p0k=0; p10 = 5; p1k = 0; p20 = 8; p2k = 2; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
				
		{
			p00 = 5; p0k=0; p10 = 11; p1k = 0; p20 = 17; p2k = 0; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		{
			p00 = 8; p0k=0; p10 = 14; p1k = 0; p20 = 20; p2k = 1; k0=0;
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {   p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		{
			p00 = 11; p0k=0; p10 = 14; p1k = 0; p20 = 29; p2k = 0; k0=0;
			//for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = -ka; kz = 1;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {  p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -1 -2* Math.sin(3*Math.PI/14); kb = 1; kz = -ka;	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3;	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kb = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kz = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
			for (int k = k0; k < 3; k++) {	p0 = p00+ (k + p0k)%3; p1 = p10 + (k + p1k)%3;	p2 = p20 + (k+p2k)%3; 	ka = -8.*Math.cos(Math.PI/7.)*Math.cos(Math.PI/7.)*Math.cos(2.*Math.PI/7.); kz = 2.*(1.+Math.cos(2*Math.PI/7)+Math.cos(4*Math.PI/7)); kb = 1+2*Math.cos(2*Math.PI/7);	p4d[np4d++] = new Point4D(ka*p4d[p0].t + kb*p4d[p1].t + kz* p4d[p2].t, ka*p4d[p0].x + kb*p4d[p1].x + kz* p4d[p2].x, ka*p4d[p0].y + kb*p4d[p1].y + kz* p4d[p2].y, ka*p4d[p0].z + kb*p4d[p1].z + kz* p4d[p2].z, curvature);		}
		}
		
		

		for (int k = 1; k<=2; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=1; k++) {
			int axisindex = 3;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 0., 1.);
		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-acosh(coshedge));

		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(acosh(coshedge));
				//p4d[i].x *= -1;
				//p4d[i].y *= -1;
				newp4d.z *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=2; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=1; k++) {
			int axisindex = 3;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		{
			double rotangle = Math.atan(p4d[2].y/p4d[2].z);
			//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(rotangle, 1., 0.);
			//System.out.println(" " + p4d[2].t+" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		}
		
		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(-acosh(coshedge));
				//p4d[i].x *= -1;
				//p4d[i].y *= -1;
				newp4d.z *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=1; k++) {
			int axisindex = 1;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.01) {
						ignore = true;
						//System.out.println("ignoring point");
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==2) System.out.println(" "+ i + " " + j + " " +index);
					edges[ne++] = new Edge(i,j);
				}
			}
		}

		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 0., 1.);
		{
		//	for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
		//	for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 0.,1.);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-2.2);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI, 0., 1.);
			//for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
		}
		
		
	}
	
	public void initB434 ( ) {
		curvature = 0;
		np4d = 0;


		int range = 3;
		
		for (int i = 0; i<= range; i++) {
			for (int j = 0; j<=range; j++ ) {
				for (int k = 0; k<=range; k++ ) {
					p4d[np4d++] = new Point4D(1,(double)i+0.5,(double)j+0.25,(double)k, curvature);
					p4d[np4d++] = new Point4D(1,(double)i+0.5,(double)j-0.25,(double)k, curvature);
					p4d[np4d++] = new Point4D(1,(double)i+0.5,(double)j,(double)k+0.25, curvature);
					p4d[np4d++] = new Point4D(1,(double)i+0.5,(double)j,(double)k-0.25, curvature);
					
					p4d[np4d++] = new Point4D(1,(double)i+0.25,(double)j+0.5,(double)k, curvature);
					p4d[np4d++] = new Point4D(1,(double)i-0.25,(double)j+0.5,(double)k, curvature);
					p4d[np4d++] = new Point4D(1,(double)i,(double)j+0.5,(double)k+0.25, curvature);
					p4d[np4d++] = new Point4D(1,(double)i,(double)j+0.5,(double)k-0.25, curvature);
					
					p4d[np4d++] = new Point4D(1,(double)i+0.25,(double)j,(double)k+0.5, curvature);
					p4d[np4d++] = new Point4D(1,(double)i-0.25,(double)j,(double)k+0.5, curvature);
					p4d[np4d++] = new Point4D(1,(double)i,(double)j+0.25,(double)k+0.5, curvature);
					p4d[np4d++] = new Point4D(1,(double)i,(double)j-0.25,(double)k+0.5, curvature);
				}	
			}
		}
		
		{
			coshedge = 1/8.;
		}
		
		int index = 0;
		ne = 0;
		
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double distance = (p4d[i].x - p4d[j].x)*(p4d[i].x - p4d[j].x) 
						+ (p4d[i].y - p4d[j].y)*(p4d[i].y - p4d[j].y) + 
						(p4d[i].z - p4d[j].z)*(p4d[i].z - p4d[j].z);  
				//if (i==0) System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( distance < coshedge * 1.03 ) {
					
					edges[index] = new Edge(i,j);
					index ++; ne ++;
					
				}
			}
		}

		for (int i = 0; i< np4d; i++) 	{
			p4d[i].rotateXYZ(Math.PI/4, 1.,0.);
			p4d[i].rotateXYZ(-Math.PI/6, 0, 1.);
		}
		
	}

	public void initB333 ( ) {
		curvature=1;
		np4d=0;
		double sqrt3 = Math.sqrt(3.);
		double sqrt6 = Math.sqrt(6.);
		double sqrt10 = Math.sqrt(10.);
		
		p4d[np4d++] = new Point4D(0,4/sqrt6,4/sqrt3,0, curvature);
		p4d[np4d++] = new Point4D(0,4/sqrt6,-2/sqrt3,2, curvature);
		p4d[np4d++] = new Point4D(0,4/sqrt6,-2/sqrt3,-2, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,1/sqrt6,4/sqrt3,0, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,1/sqrt6,-2/sqrt3,2, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,1/sqrt6,-2/sqrt3,-2, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,5/sqrt6,2/sqrt3,0, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,5/sqrt6,-1/sqrt3,1, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,5/sqrt6,-1/sqrt3,-1, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,-3/sqrt6,0,2, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,-3/sqrt6,0,-2, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,-3/sqrt6,3/sqrt3,1, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,-3/sqrt6,3/sqrt3,-1, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,-3/sqrt6,-3/sqrt3,1, curvature);
		p4d[np4d++] = new Point4D(5/sqrt10,-3/sqrt6,-3/sqrt3,-1, curvature);

		for (int i = 0; i<15; i++) {
			p4d[i+15] = new Point4D(-p4d[i].t,-p4d[i].x,-p4d[i].y,-p4d[i].z,curvature);
			np4d++;
		}
		
		
		coshedge = 1/Math.sqrt(2);
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double cosdistance = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( Math.acos(cosdistance) < Math.acos(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==0) System.out.println(" "+ i + " " + j + " " +cosdistance);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-2);
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.01, 1/Math.sqrt(2), -1/Math.sqrt(2));
		
	}
	
	public void initB343 ( ) {
		curvature=1;
		np4d=0;
		double sqrt2 = Math.sqrt(2.);
		
		for (int k = 0; k< 12; k++) {
			double[] x0to3 = {2+sqrt2, 2+sqrt2, 2+2*sqrt2,0.};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1., 1.};
			
			for (int i = 0; i< 8; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[2] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign <3; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				
				p4d[np4d] = new Point4D(x0to3sign[perms42[k][0]],x0to3sign[perms42[k][1]],x0to3sign[perms42[k][2]],x0to3sign[perms42[k][3]], curvature);
				np4d++;
				//System.out.println("Index = "+ (i+8) +" " +p4d[i+8].x + " "+p4d[i+8].y + " "+p4d[i+8].z + " "+p4d[i+8].t + " ");	
			}
		}
		
		for (int k = 0; k< 12; k++) {
			double[] x0to3 = {1+sqrt2, 1+sqrt2, 3+2*sqrt2, 1.};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1., 1.,1.};
			
			for (int i = 0; i< 16; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[2] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[3] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign <4; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				
				p4d[np4d] = new Point4D(x0to3sign[perms42[k][0]],x0to3sign[perms42[k][1]],x0to3sign[perms42[k][2]],x0to3sign[perms42[k][3]], curvature);
				np4d++;
				//System.out.println("Index = "+ (i+8) +" " +p4d[i+8].x + " "+p4d[i+8].y + " "+p4d[i+8].z + " "+p4d[i+8].t + " ");	
			}
		}
		
		coshedge = 0.9571067811865475;
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double cosdistance = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( Math.acos(cosdistance) < Math.acos(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==0) System.out.println(" "+ i + " " + j + " " +cosdistance);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.234);
		}
	}

	public void initgap ( ) {
		curvature=1;
		np4d=0;
		coshedge = tau/2;
		
		//p4d[np4d++] = new Point4D(1, 0, 0, 0, curvature);
		//p4d[np4d++] = new Point4D(0, 1, 0, 0, curvature);
		p4d[np4d++] = new Point4D(0, 0, 1, 0, curvature);
		p4d[np4d++] = new Point4D(0, 0, 0, 1, curvature);
		
		//p4d[np4d++] = new Point4D(-1, 0, 0, 0, curvature);
		//p4d[np4d++] = new Point4D(0, -1, 0, 0, curvature);
		p4d[np4d++] = new Point4D(0, 0, -1, 0, curvature);
		p4d[np4d++] = new Point4D(0, 0, 0, -1, curvature);
		
		
		for (int i = 0; i< 16; i++ ) {
			int j =i;
			double newx = (double)(j%2); j = (int)(j/2);
			double newy = (double)(j%2); j = (int)(j/2);
			double newz = (double)(j%2); j = (int)(j/2);
			double neww = (double)(j%2); j = (int)(j/2);
			newx = newx * 2 - 1;
			newy = newy * 2 - 1;
			newz = newz * 2 - 1;
			neww = neww * 2 - 1;
			p4d[np4d] = new Point4D(newx, newy, newz, neww, curvature); np4d++;
			//System.out.println("Index = "+ (i+8) +" " +p4d[i+8].x + " "+p4d[i+8].y + " "+p4d[i+8].z + " "+p4d[i+8].t + " ");	
		}
		
		//System.out.println(p4d[4].x);
		//np4d = 24;
		
		for (int k = 0; k< 12; k++) {
			double[] x0to3 = {tau, 1., 1./tau,0.};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1., 1.};
			
			for (int i = 0; i< 8; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[2] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign <3; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				double newt = x0to3sign[perma4[k][0]];
				double newx = x0to3sign[perma4[k][1]];
				double newy = x0to3sign[perma4[k][2]];
				double newz = x0to3sign[perma4[k][3]];
				if (Math.abs(newx)<1e-10 && Math.abs(Math.abs(newt) - tau)<1e-10 && newy*newz>0) continue;
				if (Math.abs(newx)<1e-10 && Math.abs(Math.abs(newt) - 1./tau)<1e-10 && newy*newz>0) continue;
				if (Math.abs(newt)<1e-10 && Math.abs(Math.abs(newx) - tau)<1e-10 && newy*newz < 0) continue;
				if (Math.abs(newt)<1e-10 && Math.abs(Math.abs(newx) - 1./tau)<1e-10 && newy*newz < 0) continue;
				p4d[np4d] = new Point4D(newt,newx,newy,newz, curvature);
				np4d++;
				//System.out.println("Index = "+ (i+8) +" " +p4d[i+8].x + " "+p4d[i+8].y + " "+p4d[i+8].z + " "+p4d[i+8].t + " ");	
			}
		}
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double cosdistance = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( Math.acos(cosdistance) < Math.acos(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==0) System.out.println(" "+ i + " " + j + " " +cosdistance);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/3, 1, 0);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
		}
				
	}
	
	public void initClif ( ) {
		curvature = 1;
		np4d = 0;

		int npoints = 20;

		for (int i = 0; i< npoints; i++) {
			for (int j = 0; j< npoints; j++ ) {
				p4d[np4d] = new Point4D(
						(double)(Math.sin(2*Math.PI/npoints*i)),
						(double)(Math.cos(2*Math.PI/npoints*i)),
						(double)(Math.sin(2*Math.PI/npoints*j)),
						(double)(Math.cos(2*Math.PI/npoints*j)),
						curvature);
				np4d++;
			}
		}
		
		ne = 0;
		
		for (int i = 0; i< npoints; i++ ) {
			for (int j = 0; j< npoints; j++ ) {
				edges[ne] = new Edge(i*npoints + j, i*npoints + (j+1)%npoints); ne++;
				edges[ne] = new Edge(i*npoints + j, ((i+1)%npoints)*npoints + j); ne++;
			}
		}

		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(1.2);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(1, 1, 0);
		}
	}

	public void init635 ( ) {
		curvature =  -1;
		coshedge = 3./2 * Math.sqrt(5)* tau - 1.;
		double sinhedge = Math.sqrt(coshedge*coshedge - 1);
		
		np4d=0;
		p4d[0] = new Point4D(1,0,0,0, curvature);
		p4d[1] = new Point4D(coshedge,sinhedge/Math.sqrt(tau + 2) * tau,sinhedge/Math.sqrt(tau + 2),0, curvature);
		p4d[2] = new Point4D(coshedge,0, sinhedge/Math.sqrt(tau + 2) * tau,sinhedge/Math.sqrt(tau + 2), curvature);
		p4d[3] = new Point4D(coshedge,sinhedge/Math.sqrt(tau + 2), 0, sinhedge/Math.sqrt(tau + 2) * tau, curvature);

		np4d=4;
				
		for (int k = 0; k < 3; k++) {
			int p0 = 0; 
			int p1 = 1 + (k)%3; 
			int p2 = 1 + (k+1)%3;					
			p4d[np4d] = new Point4D(
					2*p4d[p1].t + p4d[p2].t - 2* p4d[p0].t,
					2*p4d[p1].x + p4d[p2].x - 2* p4d[p0].x, 
					2*p4d[p1].y + p4d[p2].y - 2* p4d[p0].y, 
					2*p4d[p1].z + p4d[p2].z - 2* p4d[p0].z, 
					curvature);
			np4d ++;
		}

		for (int k = 0; k < 3; k++) {
			int p0 = 0; 
			int p1 = 1 + (k)%3; 
			int p2 = 1 + (k+1)%3;					
			p4d[np4d] = new Point4D(
					1*p4d[p1].t + 2*p4d[p2].t - 2* p4d[p0].t,
					1*p4d[p1].x + 2*p4d[p2].x - 2* p4d[p0].x, 
					1*p4d[p1].y + 2*p4d[p2].y - 2* p4d[p0].y, 
					1*p4d[p1].z + 2*p4d[p2].z - 2* p4d[p0].z, 
					curvature);
			np4d ++;
		}

		for (int k = 0; k < 3; k++) {
			int p0 = 0; 
			int p1 = 1 + (k)%3; 
			int p2 = 1 + (k+1)%3;					
			p4d[np4d] = new Point4D(
					2*p4d[p1].t + 2*p4d[p2].t - 3* p4d[p0].t,
					2*p4d[p1].x + 2*p4d[p2].x - 3* p4d[p0].x, 
					2*p4d[p1].y + 2*p4d[p2].y - 3* p4d[p0].y, 
					2*p4d[p1].z + 2*p4d[p2].z - 3* p4d[p0].z, 
					curvature);
			np4d ++;
		}
		
		for (int k = 0; k < 3; k++) {
			int p0 = 1 + k; 
			int p1 = 4 + k; 
			int p2 = 7 + (k+2)%3;					
			p4d[np4d] = new Point4D(
					2*p4d[p1].t + 1*p4d[p2].t - 2* p4d[p0].t,
					2*p4d[p1].x + 1*p4d[p2].x - 2* p4d[p0].x, 
					2*p4d[p1].y + 1*p4d[p2].y - 2* p4d[p0].y, 
					2*p4d[p1].z + 1*p4d[p2].z - 2* p4d[p0].z, 
					curvature);
			np4d ++;
		}
		
		for (int k = 0; k < 3; k++) {
			int p0 = 1 + k; 
			int p1 = 4 + k; 
			int p2 = 7 + (k+2)%3;						
			p4d[np4d] = new Point4D(
					1*p4d[p1].t + 2*p4d[p2].t - 2* p4d[p0].t,
					1*p4d[p1].x + 2*p4d[p2].x - 2* p4d[p0].x, 
					1*p4d[p1].y + 2*p4d[p2].y - 2* p4d[p0].y, 
					1*p4d[p1].z + 2*p4d[p2].z - 2* p4d[p0].z, 
					curvature);
			np4d ++;
		}

		for (int k = 0; k < 3; k++) {
			int p0 = 1 + k; 
			int p1 = 4 + k; 
			int p2 = 7 + (k+2)%3;					
			p4d[np4d] = new Point4D(
					2*p4d[p1].t + 2*p4d[p2].t - 3* p4d[p0].t,
					2*p4d[p1].x + 2*p4d[p2].x - 3* p4d[p0].x, 
					2*p4d[p1].y + 2*p4d[p2].y - 3* p4d[p0].y, 
					2*p4d[p1].z + 2*p4d[p2].z - 3* p4d[p0].z, 
					curvature);
			np4d ++;
		}
		
		for (int k = 0; k < 3; k++) {
			int p0 = 4 + k; 
			int p1 = 10 + k; 
			int p2 = 13 + (k)%3;					
			p4d[np4d] = new Point4D(
					2*p4d[p1].t + 1*p4d[p2].t - 2* p4d[p0].t,
					2*p4d[p1].x + 1*p4d[p2].x - 2* p4d[p0].x, 
					2*p4d[p1].y + 1*p4d[p2].y - 2* p4d[p0].y, 
					2*p4d[p1].z + 1*p4d[p2].z - 2* p4d[p0].z, 
					curvature);
			np4d ++;
		}
		
		for (int k = 0; k < 3; k++) {
			int p0 = 4 + k; 
			int p1 = 10 + k; 
			int p2 = 13 + (k)%3;
			p4d[np4d] = new Point4D(
					1*p4d[p1].t + 2*p4d[p2].t - 2* p4d[p0].t,
					1*p4d[p1].x + 2*p4d[p2].x - 2* p4d[p0].x, 
					1*p4d[p1].y + 2*p4d[p2].y - 2* p4d[p0].y, 
					1*p4d[p1].z + 2*p4d[p2].z - 2* p4d[p0].z, 
					curvature);
			np4d ++;
		}

		for (int k = 0; k < 3; k++) {
			int p0 = 4 + k; 
			int p1 = 10 + k; 
			int p2 = 13 + (k)%3;
			p4d[np4d] = new Point4D(
					2*p4d[p1].t + 2*p4d[p2].t - 3* p4d[p0].t,
					2*p4d[p1].x + 2*p4d[p2].x - 3* p4d[p0].x, 
					2*p4d[p1].y + 2*p4d[p2].y - 3* p4d[p0].y, 
					2*p4d[p1].z + 2*p4d[p2].z - 3* p4d[p0].z, 
					curvature);
			np4d ++;
		}
		
		for (int k = 0; k < 3; k++) {
			int p0 = 7 + k; 
			int p1 = 10 + k; 
			int p2 = 16 + (k+1)%3;
			p4d[np4d] = new Point4D(
					1*p4d[p1].t + 2*p4d[p2].t - 2* p4d[p0].t,
					1*p4d[p1].x + 2*p4d[p2].x - 2* p4d[p0].x, 
					1*p4d[p1].y + 2*p4d[p2].y - 2* p4d[p0].y, 
					1*p4d[p1].z + 2*p4d[p2].z - 2* p4d[p0].z, 
					curvature);
			np4d ++;
		}

		for (int k = 0; k < 3; k++) {
			int p0 = 7 + k; 
			int p1 = 10 + k; 
			int p2 = 16 + (k+1)%3;
			p4d[np4d] = new Point4D(
					2*p4d[p1].t + 2*p4d[p2].t - 3* p4d[p0].t,
					2*p4d[p1].x + 2*p4d[p2].x - 3* p4d[p0].x, 
					2*p4d[p1].y + 2*p4d[p2].y - 3* p4d[p0].y, 
					2*p4d[p1].z + 2*p4d[p2].z - 3* p4d[p0].z, 
					curvature);
			np4d ++;
		}
		
		
		
		
		
		for (int k = 1; k<=4; k++) {
			int axisindex = 1;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=4; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=4; k++) {
			int axisindex = 3;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		
		
		
		
		//System.out.println("The number of vertices is: " + np4d);
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.atan(tau), 1, 0);
		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-acosh(coshedge));


		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(acosh(coshedge));
				
				//double newx = p4d[i].x * Math.cos(Math.PI) + p4d[i].y * Math.sin(Math.PI);
				//double newy = -p4d[i].x * Math.sin(Math.PI) + p4d[i].y * Math.cos(Math.PI);
				
				p4d[i].x *= -1;
				p4d[i].y *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		//System.out.println("The number of vertices is: " + np4d);
		
		
		
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==2) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		
		
		//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		//System.out.println("The number of vertices is: " + np4d);
		//System.out.println("The number of edges is: " + ne);
		
		
		
		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
		
		{
			//for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.6, 1, 0);
			
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.3, -1, 0.7);
		}
		
		
	}
	
	public void init633 ( ) {
		curvature = -1;
		int range = 24;
		np4d = 0;
		coshedge = 5./4;
		double sqrt3 = Math.sqrt(3);
		
		for (int x1 = -range; x1 <= range; x1++) {
			for (int x2 = -range; x2 <= range; x2++) {
				for (int x3 = -range; x3 <= range; x3++) {
					double testdouble = Math.sqrt((double)(3*x1*x1 + 3*x2*x2 + 3*x3*x3+16));
					
					if (testdouble == (int)(testdouble) && ((int)(testdouble) + x1 + x2 + x3)%4==0 ) {
						p4d[np4d] = new Point4D(testdouble,(double)(x1*sqrt3),(double)(x2*sqrt3),(double)(x3*sqrt3), curvature);
						np4d ++;
						//System.out.println(" " + (int)testdouble + " "+ x1+ " "+ x2+ " "+ x3);
					}
				}
			}
		}
		
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==7 || j==7) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}

		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(1.);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/4, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
		}
	}
	
	public void init634 ( ) {
		curvature = -1;
		int range = 13;
		np4d = 0;
		coshedge = 2.;
		double sqrt3 = Math.sqrt(3);
		
		for (int x1 = -range; x1 <= range; x1++) {
			for (int x2 = -range; x2 <= range; x2++) {
				for (int x3 = -range; x3 <= range; x3++) {
					double testdouble = Math.sqrt((double)(3*x1*x1 + 3*x2*x2 + 3*x3*x3+1));
					if (testdouble == (int)(testdouble)) {
						p4d[np4d] = new Point4D(testdouble,(double)(x1*sqrt3),(double)(x2*sqrt3),(double)(x3*sqrt3), curvature);
						np4d ++;
						//System.out.println(" " + (int)testdouble + " "+ x1+ " "+ x2+ " "+ x3);
					}
				}
			}
		}
		
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==7 || j==7) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}

		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
		}
		
	}
	
	public void init443 ( ) {
		curvature = -1;
		int range = 18;
		np4d = 0;
		coshedge = 2.;
		
		for (int x1 = -range; x1 <= range; x1++) {
			for (int x2 = -range; x2 <= range; x2++) {
				for (int x3 = -range; x3 <= range; x3++) {
					double testdouble = Math.sqrt((double)(x1*x1 + x2*x2 + x3*x3+1));
					if (testdouble == (int)(testdouble)) {
						p4d[np4d] = new Point4D(testdouble,(double)x1,(double)x2,(double)x3, curvature);
						np4d ++;
						//System.out.println(" " + (int)testdouble + " "+ x1+ " "+ x2+ " "+ x3);
					}
				}
			}
		}
		
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==7 || j==7) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}

		
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(2.);
			for (int i = 0; i< np4d; i++) 	{
				double newx = (p4d[i].x + p4d[i].y)/Math.sqrt(2);
				double newy = (p4d[i].x - p4d[i].y)/Math.sqrt(2);
				p4d[i].x = newx;
				p4d[i].y = newy;
				p4d[i].update2d();
			}
		}
		
		
	}
	
	public void init434 ( ) {
		curvature = 0;
		np4d = 0;


		int range = 2;
		
		for (int i = -range; i<= range; i++) {
			for (int j = -range; j<=range; j++ ) {
				for (int k = -range; k<=range; k++ ) {
					p4d[np4d] = new Point4D(1,(double)i,(double)j,(double)k, curvature);
					np4d++;
				}	
			}
		}
		
		{
			coshedge = 1;
		}
		
		int index = 0;
		ne = 0;
		
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double distance = (p4d[i].x - p4d[j].x)*(p4d[i].x - p4d[j].x) 
						+ (p4d[i].y - p4d[j].y)*(p4d[i].y - p4d[j].y) + 
						(p4d[i].z - p4d[j].z)*(p4d[i].z - p4d[j].z);  
				//if (i==0) System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( distance < coshedge * 1.03 ) {
					
					edges[index] = new Edge(i,j);
					index ++; ne ++;
					
				}
			}
		}

		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(1.3);
		}
	}

	public void init533 ( ) {
		curvature=1;
		np4d = 0;

		for (int k = 0; k< 6; k++) {
			double[] x0to3 = {1., 1., 0. ,0.};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1.};
			
			for (int i = 0; i< 4; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign <2; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				
				p4d[np4d] = new Point4D(x0to3sign[choose42[k][0]],x0to3sign[choose42[k][1]],x0to3sign[choose42[k][2]],x0to3sign[choose42[k][3]], curvature);
				np4d++;
			}
		}


		
		for (int k = 0; k< 4; k++) {
			double[] x0to3 = {1., 1., 1. ,Math.sqrt(5.)};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1., 1., 1.};
			
			for (int i = 0; i< 16; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[2] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[3] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign < sign.length; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				
				p4d[np4d] = new Point4D(x0to3sign[cycle4[k][0]],x0to3sign[cycle4[k][1]],x0to3sign[cycle4[k][2]],x0to3sign[cycle4[k][3]], curvature);
				np4d++;
			}
		}		
		
		for (int k = 0; k< 4; k++) {
			double[] x0to3 = {1./tau/tau, tau, tau ,tau};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1., 1., 1.};
			
			for (int i = 0; i< 16; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[2] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[3] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign < sign.length; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				
				p4d[np4d] = new Point4D(x0to3sign[cycle4[k][0]],x0to3sign[cycle4[k][1]],x0to3sign[cycle4[k][2]],x0to3sign[cycle4[k][3]], curvature);
				np4d++;
			}
		}	
		
		for (int k = 0; k< 4; k++) {
			double[] x0to3 = {ttau, 1/tau, 1/tau ,1/tau};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1., 1., 1.};
			
			for (int i = 0; i< 16; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[2] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[3] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign < sign.length; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				
				p4d[np4d] = new Point4D(x0to3sign[cycle4[k][0]],x0to3sign[cycle4[k][1]],x0to3sign[cycle4[k][2]],x0to3sign[cycle4[k][3]], curvature);
				np4d++;
			}
		}	
		
		for (int k = 0; k< 12; k++) {
			double[] x0to3 = {1/ttau, 1., ttau , 0};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1., 1.};
			
			for (int i = 0; i< 8; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[2] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign < sign.length; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				
				p4d[np4d] = new Point4D(x0to3sign[perma4[k][0]],x0to3sign[perma4[k][1]],x0to3sign[perma4[k][2]],x0to3sign[perma4[k][3]], curvature);
				np4d++;
			}
		}	

		for (int k = 0; k< 12; k++) {
			double[] x0to3 = {1/tau, tau, Math.sqrt(5), 0};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1., 1.};
			
			for (int i = 0; i< 8; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[2] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign < sign.length; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				
				p4d[np4d] = new Point4D(x0to3sign[perma4[k][0]],x0to3sign[perma4[k][1]],x0to3sign[perma4[k][2]],x0to3sign[perma4[k][3]], curvature);
				np4d++;
			}
		}	
		
		
		for (int k = 0; k< 12; k++) {
			double[] x0to3 = {2., 1/tau, 1., tau};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1., 1., 1.};
			
			for (int i = 0; i< 16; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[2] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[3] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign < sign.length; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				
				p4d[np4d] = new Point4D(x0to3sign[perma4[k][0]],x0to3sign[perma4[k][1]],x0to3sign[perma4[k][2]],x0to3sign[perma4[k][3]], curvature);
				np4d++;
			}
		}	
		
		{
			int i = 0; int j = 344;
			coshedge = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;
		}
		
		int index = 0;
		ne = 0;
		
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double cosdistance = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;  
				//if (i==0) System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( cosdistance > coshedge * 0.97 ) {
					
					edges[index] = new Edge(i,j);
					index ++; ne ++;
					
				}
			}
		}

		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
		}
	}

	public void init335 ( ) {
		curvature=1;
		
		p4d[0] = new Point4D(1, 0, 0, 0, curvature);
		p4d[1] = new Point4D(0, 1, 0, 0, curvature);
		p4d[2] = new Point4D(0, 0, 1, 0, curvature);
		p4d[3] = new Point4D(0, 0, 0, 1, curvature);
		
		p4d[4] = new Point4D(-1, 0, 0, 0, curvature);
		p4d[5] = new Point4D(0, -1, 0, 0, curvature);
		p4d[6] = new Point4D(0, 0, -1, 0, curvature);
		p4d[7] = new Point4D(0, 0, 0, -1, curvature);
		
		for (int i = 0; i< 16; i++ ) {
			int j =i;
			double newx = (double)(j%2); j = (int)(j/2);
			double newy = (double)(j%2); j = (int)(j/2);
			double newz = (double)(j%2); j = (int)(j/2);
			double neww = (double)(j%2); j = (int)(j/2);
			newx = newx * 2 - 1;
			newy = newy * 2 - 1;
			newz = newz * 2 - 1;
			neww = neww * 2 - 1;
			p4d[i+8] = new Point4D(newx, newy, newz, neww, curvature);
			//System.out.println("Index = "+ (i+8) +" " +p4d[i+8].x + " "+p4d[i+8].y + " "+p4d[i+8].z + " "+p4d[i+8].t + " ");	
		}
		
		//System.out.println(p4d[4].x);
		np4d = 24;
		
		for (int k = 0; k< 12; k++) {
			double[] x0to3 = {tau, 1., 1./tau,0.};
			double[] x0to3sign = {0.,0.,0.,0.};
			double[] sign = {1., 1., 1.};
			
			for (int i = 0; i< 8; i++ ) {
				int j =i;
				sign[0] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[1] = (double)(j%2)*2-1; j = (int)(j/2);
				sign[2] = (double)(j%2)*2-1; j = (int)(j/2);

				for (int indexsign = 0; indexsign <3; indexsign ++) x0to3sign[indexsign] = x0to3[indexsign] * sign[indexsign];
				
				p4d[np4d] = new Point4D(x0to3sign[perma4[k][0]],x0to3sign[perma4[k][1]],x0to3sign[perma4[k][2]],x0to3sign[perma4[k][3]], curvature);
				np4d++;
				//System.out.println("Index = "+ (i+8) +" " +p4d[i+8].x + " "+p4d[i+8].y + " "+p4d[i+8].z + " "+p4d[i+8].t + " ");	
			}
		}
		
		
		{
			int i = 0; int j = 37;
			coshedge = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;
		}
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double cosdistance = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( Math.acos(cosdistance) < Math.acos(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==0) System.out.println(" "+ i + " " + j + " " +cosdistance);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
		}
		
	}
	
	public void init343 ( ) {
		curvature=1;
		
		p4d[0] = new Point4D(1, 0, 0, 0, curvature);
		p4d[1] = new Point4D(0, 1, 0, 0, curvature);
		p4d[2] = new Point4D(0, 0, 1, 0, curvature);
		p4d[3] = new Point4D(0, 0, 0, 1, curvature);
		
		p4d[4] = new Point4D(-1, 0, 0, 0, curvature);
		p4d[5] = new Point4D(0, -1, 0, 0, curvature);
		p4d[6] = new Point4D(0, 0, -1, 0, curvature);
		p4d[7] = new Point4D(0, 0, 0, -1, curvature);
		
		for (int i = 0; i< 16; i++ ) {
			int j =i;
			double newx = (double)(j%2); j = (int)(j/2);
			double newy = (double)(j%2); j = (int)(j/2);
			double newz = (double)(j%2); j = (int)(j/2);
			double neww = (double)(j%2); j = (int)(j/2);
			newx = newx * 2 - 1;
			newy = newy * 2 - 1;
			newz = newz * 2 - 1;
			neww = neww * 2 - 1;
			p4d[i+8] = new Point4D(newx, newy, newz, neww, curvature);
			//System.out.println("Index = "+ (i+8) +" " +p4d[i+8].x + " "+p4d[i+8].y + " "+p4d[i+8].z + " "+p4d[i+8].t + " ");	
		}
		
		//System.out.println(p4d[4].x);
		np4d = 24;
		{
			int i = 0; int j = 23;
			coshedge = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;
		}
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double cosdistance = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( Math.acos(cosdistance) < Math.acos(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==7 || j==7) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(3), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(3), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.2);
		}
	}

	public void init334 ( ) {
		curvature=1;
		
		p4d[0] = new Point4D(1, 0, 0, 0, curvature);
		p4d[1] = new Point4D(0, 1, 0, 0, curvature);
		p4d[2] = new Point4D(0, 0, 1, 0, curvature);
		p4d[3] = new Point4D(0, 0, 0, 1, curvature);
		
		p4d[4] = new Point4D(-1, 0, 0, 0, curvature);
		p4d[5] = new Point4D(0, -1, 0, 0, curvature);
		p4d[6] = new Point4D(0, 0, -1, 0, curvature);
		p4d[7] = new Point4D(0, 0, 0, -1, curvature);
		
		//System.out.println(p4d[4].x);
		np4d = 8;
		{
			int i = 1; int j = 2;
			coshedge = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;
		}
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double cosdistance = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( Math.acos(cosdistance) < Math.acos(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==7 || j==7) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/3, 1/Math.sqrt(2), -1/Math.sqrt(2));
		}
	}
	
	public void init333 ( ) {
		curvature=1;
		
		p4d[0] = new Point4D(1/Math.sqrt(10), 1/Math.sqrt(6), 1/Math.sqrt(3), 1, curvature);
		p4d[1] = new Point4D(1/Math.sqrt(10), 1/Math.sqrt(6), 1/Math.sqrt(3), -1, curvature);
		p4d[2] = new Point4D(1/Math.sqrt(10), 1/Math.sqrt(6), -2/Math.sqrt(3), 0, curvature);
		p4d[3] = new Point4D(1/Math.sqrt(10), -Math.sqrt(3./2), 0, 0, curvature);
		p4d[4] = new Point4D(-2*Math.sqrt(2./5),0,0,0, curvature);
		//System.out.println(p4d[4].x);
		np4d = 5;
		{
			int i = 1; int j = 2;
			coshedge = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;
		}
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double cosdistance = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( Math.acos(cosdistance) < Math.acos(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==7 || j==7) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		
		
		//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		//System.out.println("The number of vertices is: " + np4d);
		//System.out.println("The number of edges is: " + ne);
		//for (int i = 0; i< np4d; i++) System.out.println(p4d[i].t +" " + p4d[i].x +" " + p4d[i].y +" " + p4d[i].z+" " + p4d[i].dist+" " + p4d[i].pointsize);
			
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/4, 0, 1);
		}
		
	}
	
	public void init433 ( ) {
		
		
		//System.out.println(a + "  " + b);
		
		for (int i = 0; i< 16; i++ ) {
			int j =i;
			double newx = (double)(j%2); j = (int)(j/2);
			double newy = (double)(j%2); j = (int)(j/2);
			double newz = (double)(j%2); j = (int)(j/2);
			double neww = (double)(j%2); j = (int)(j/2);
			newx = newx * 2 - 1;
			newy = newy * 2 - 1;
			newz = newz * 2 - 1;
			neww = neww * 2 - 1;
			p4d[i] = new Point4D(newx, newy, newz, neww, curvature);
			//System.out.println(p4d[i].x + " "+p4d[i].y + " "+p4d[i].z + " "+p4d[i].t + " ");	
		}
		np4d = 16;
		coshedge = 0.5;
		
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double cosdistance = p4d[i].t * p4d[j].t + p4d[i].x * p4d[j].x + p4d[i].y * p4d[j].y  + p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +cosdistance);
				if ( Math.acos(cosdistance) < Math.acos(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==7 || j==7) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		
		
		//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		//System.out.println("The number of vertices is: " + np4d);
		//System.out.println("The number of edges is: " + ne);
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.2);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
		}
		
	}
	
	public void init353 ( ) {
		
		coshedge = (5 + 3 * Math.sqrt(5))/4.;
		double a = tau + 0.5;
		double b = Math.sqrt(ttau*ttau-3)/2.;
		//System.out.println(a + "  " + b);
		
		
		p4d[0] = new Point4D(a,b*tau,b,0, curvature);
		p4d[1] = new Point4D(a,b*tau,-b,0, curvature);
		p4d[2] = new Point4D(a,-b*tau,b,0, curvature);
		p4d[3] = new Point4D(a,-b*tau,-b,0, curvature);
		
		p4d[4] = new Point4D(a,0,b*tau,b, curvature);
		p4d[5] = new Point4D(a,0,b*tau,-b, curvature);
		p4d[6] = new Point4D(a,0,-b*tau,b, curvature);
		p4d[7] = new Point4D(a,0,-b*tau,-b, curvature);
		
		p4d[8] = new Point4D(a,b,0,b*tau, curvature);
		p4d[9] = new Point4D(a,b,0,-b*tau, curvature);
		p4d[10] = new Point4D(a,-b,0,b*tau, curvature);
		p4d[11] = new Point4D(a,-b,0,-b*tau, curvature);
		
		np4d=12;
		
		
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.atan(tau), 1, 0);
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ( acosh(a));
		{
			double yzratio = p4d[6].y/p4d[6].z;
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.atan(yzratio), 1, 0);
		}
		
		//System.out.println(" " + p4d[7].x +" " + p4d[7].y +" " + p4d[7].z);
		//System.out.println(" " + p4d[6].x +" " + p4d[6].y +" " + p4d[6].z);	
		
		
		
		for (int k = 1; k<=2; k++) {
			int axisindex = 6;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		
		for (int k = 1; k<=2; k++) {
			int axisindex = 11;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		
		
		for (int k = 1; k<=2; k++) {
			int axisindex = 50;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/3);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		
		
		

		//System.out.println("The number of vertices is: " + np4d);
		
		

		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(acosh(coshedge));
				
				
				p4d[i].x *= -1;
				p4d[i].y *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
					np4d ++;
				}
			}
		}
		
		
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==7 || j==7) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		
		

		
		{
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.3, -1, 0.7);
		}
		
		
	}
	
	public void init535 ( ) {
		coshedge = 3./2 + Math.sqrt(5); // also equal to (phi4 + 1/phi)/2 = (4 * phi + 1) / 2
		double R = Math.sqrt(2.0/19*(7.0*Math.sqrt(5)-13));
		
//		System.out.println((4*phi+8)/(24*phi+13)); // this is R*R
		
		double x01 = R*coshedge;
		double x02 = Math.sqrt(7*ttau + R*R	);
		double x03 = Math.sqrt(4*ttau*(2*ttau+1) + R*R);
		double x04 = Math.sqrt((3*tau+1)*(3*tau+1)+(3*tau+2)*(3*tau+2)+(2*tau+2)*(2*tau+2) + R*R);
		double x05 = Math.sqrt((3*tau+2)*(3*tau+2)*3 + R*R);
		
		System.out.println(x02 / x01);
		
		System.out.println(Math.sqrt((431*phi+267)/(89*phi+58)));
		
		System.out.println((13*phi+3)/11);
		
		
		p4d[0] = new Point4D(R, 0,0,0, curvature);
		
		p4d[1] = new Point4D(x01,tau,1,0, curvature);
		p4d[2] = new Point4D(x01,0,tau,1, curvature);
		p4d[3] = new Point4D(x01,1,0,tau, curvature);
		
		np4d = 4;
		
		p4d[4] = new Point4D(x02,ttau,2*tau,1, curvature);
		p4d[5] = new Point4D(x02,1,ttau,2*tau, curvature);
		p4d[6] = new Point4D(x02,2*tau,1,ttau, curvature);
		
		p4d[7] = new Point4D(x02,1+ttau,tau,tau, curvature);
		p4d[8] = new Point4D(x02,tau,1+ttau,tau, curvature);
		p4d[9] = new Point4D(x02,tau,tau,1+ttau, curvature);
		np4d = 10;
		
		p4d[10] = new Point4D(x03,2*tau+2,2*tau+2,2*tau, curvature);
		p4d[11] = new Point4D(x03,2*tau+2,2*tau,2*tau+2, curvature);
		p4d[12] = new Point4D(x03,2*tau,2*tau+2,2*tau+2, curvature);

		p4d[13] = new Point4D(x03,3*tau+1,2*tau+1,tau+2, curvature);
		p4d[14] = new Point4D(x03,tau+2,3*tau+1,2*tau+1, curvature);
		p4d[15] = new Point4D(x03,2*tau+1,tau+2,3*tau+1, curvature);
		
		np4d=16;
		
		p4d[16] = new Point4D(x04,3*tau+1,3*tau+2,2*tau+2, curvature);
		p4d[17] = new Point4D(x04,2*tau+2,3*tau+1,3*tau+2, curvature);
		p4d[18] = new Point4D(x04,3*tau+2,2*tau+2,3*tau+1, curvature);
		
		p4d[19] = new Point4D(x05,3*tau+2,3*tau+2,3*tau+2, curvature);
		
		np4d=20;
		
//		
//		
//		for (int k = 1; k<=4; k++) {
//			int axisindex = 1;
//			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
//			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
//			int currentnp = np4d;
//			for (int i = 0; i<currentnp; i++) {
//				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
//				p3d.rotate3D(axis, Math.PI*2/5);
//
//				boolean ignore = false;
//				for (int j=0; j<currentnp; j++) {
//					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
//							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
//							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
//					if (distance < 0.1) {
//						ignore = true;
//						break;
//					}
//				}
//				if (!ignore) {
//					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
//					np4d ++;
//				}
//			}
//		}
//		
//		for (int k = 1; k<=4; k++) {
//			int axisindex = 2;
//			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
//			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
//			int currentnp = np4d;
//			for (int i = 0; i<currentnp; i++) {
//				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
//				p3d.rotate3D(axis, Math.PI*2/5);
//
//				boolean ignore = false;
//				for (int j=0; j<currentnp; j++) {
//					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
//							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
//							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
//					if (distance < 0.1) {
//						ignore = true;
//						break;
//					}
//				}
//				if (!ignore) {
//					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
//					np4d ++;
//				}
//			}
//		}
//		
//		for (int k = 1; k<=4; k++) {
//			int axisindex = 3;
//			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
//			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
//			int currentnp = np4d;
//			for (int i = 0; i<currentnp; i++) {
//				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
//				p3d.rotate3D(axis, Math.PI*2/5);
//
//				boolean ignore = false;
//				for (int j=0; j<currentnp; j++) {
//					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
//							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
//							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
//					if (distance < 0.1) {
//						ignore = true;
//						break;
//					}
//				}
//				if (!ignore) {
//					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
//					np4d ++;
//				}
//			}
//		}
		
		
		
		
		
		
		System.out.println("The number of vertices is: " + np4d);
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.atan(tau), 1, 0);
		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-acosh(coshedge));


		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(acosh(coshedge));
				
				//double newx = p4d[i].x * Math.cos(Math.PI) + p4d[i].y * Math.sin(Math.PI);
				//double newy = -p4d[i].x * Math.sin(Math.PI) + p4d[i].y * Math.cos(Math.PI);
				
				p4d[i].x *= -1;
				p4d[i].y *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
//				if (!ignore) {
//					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
//					np4d ++;
//				}
			}
		}
		
		
		//System.out.println("The number of vertices is: " + np4d);
		
		
		/*
		for (int k = 1; k<=4; k++) {
			int axisindex = 20;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z);
					np4d ++;
				}
			}
		}
		
		/*
		for (int k = 1; k<=4; k++) {
			int axisindex = 191;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z);
					np4d ++;
				}
			}
		}
		*/
		/*
		{
			int currentnp = np4d;
			for (int i = 0; i< currentnp-1; i++ ) {
				for (int j = i+1; j< currentnp; j++ ) {
					double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
					//System.out.println(" "+ i + " " + j + " " +distance);
					if ( (coshdistance - coshedge) < 0.01 && (coshdistance - coshedge) > -0.01) {
						for (int k=0; k<nedgepoints; k++) {
							double newt = p4d[i].t * (k+1)/(nedgepoints+1) + p4d[j].t * (nedgepoints-k)/(nedgepoints+1);
							double newx = p4d[i].x * (k+1)/(nedgepoints+1) + p4d[j].x * (nedgepoints-k)/(nedgepoints+1);
							double newy = p4d[i].y * (k+1)/(nedgepoints+1) + p4d[j].y * (nedgepoints-k)/(nedgepoints+1);
							double newz = p4d[i].z * (k+1)/(nedgepoints+1) + p4d[j].z * (nedgepoints-k)/(nedgepoints+1);
							//System.out.println("" + np4d);
							p4d[np4d] = new Point4D(newt,newx, newy, newz);

							np4d ++;
						}
					}
				}
			}
		}
		*/
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==2) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		
		
		//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		//System.out.println("The number of vertices is: " + np4d);
		//System.out.println("The number of edges is: " + ne);
		
		
		
		{
			//for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.6, 1, 0);
			
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.3, -1, 0.7);
		}
		
		
	}
	
	public void init435 ( ) {
		coshedge = ttau;
		p4d[0] = new Point4D(1,0,0,0, curvature);
		p4d[1] = new Point4D(tau32,tau,1,0, curvature);
		p4d[2] = new Point4D(tau32,0,tau,1, curvature);
		p4d[3] = new Point4D(tau32,1,0,tau, curvature);
		
		p4d[4] = new Point4D(tau52,1,tau,ttau, curvature);
		p4d[5] = new Point4D(tau52,tau,ttau,1, curvature);
		p4d[6] = new Point4D(tau52,ttau,1,tau, curvature);
		
		p4d[7] = new Point4D(tau32*(2*tau-1),ttau,ttau,ttau, curvature);

		np4d = 8;
		//System.out.println("The number of vertices is: " + np4d);
		
		for (int k = 1; k<=4; k++) {
			int axisindex = 1;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=4; k++) {
			int axisindex = 2;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		for (int k = 1; k<=4; k++) {
			int axisindex = 3;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z, curvature);
					np4d ++;
				}
			}
		}
		
		/*
		// generate new points by mirroring
		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				if (p4d[i].x > 1e-10) {
					p4d[np4d] = new Point4D(p4d[i].t,-p4d[i].x,p4d[i].y,p4d[i].z);
					np4d ++;
				}
			}
		}
		
		
		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				if (p4d[i].y > 1e-10) {
					p4d[np4d] = new Point4D(p4d[i].t,p4d[i].x,-p4d[i].y,p4d[i].z);
					np4d ++;
				}
			}
		}
		
		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				if (p4d[i].z > 1e-10) {
					p4d[np4d] = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,-p4d[i].z);
					np4d ++;
				}
			}
		}

		
		{
			int currentnp = np4d;
			
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(acosh(tau));
			
			for (int i = 0; i<currentnp; i++) {
				
				if (p4d[i].z > 1e-10) {
					p4d[np4d] = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,-p4d[i].z);
					np4d ++;
				}
			}
		}
		
		/*
		{
			
			int currentnp = np4d;
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2,1,0);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(acosh(tau));
			
			for (int i = 0; i<currentnp; i++) {
				
				if (p4d[i].z > 1e-10) {
					p4d[np4d] = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,-p4d[i].z);
					np4d ++;
				}
			}
		}
		*/
		
		//System.out.println("The number of vertices is: " + np4d);

		/*
		int index = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( (coshdistance - tau) < 0.01 && (coshdistance - tau) > -0.01) {
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}

		
				
		{
			int currentnp = np4d;
			for (int i = 0; i< currentnp-1; i++ ) {
				for (int j = i+1; j< currentnp; j++ ) {
					double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
					//System.out.println(" "+ i + " " + j + " " +distance);
					if ( (coshdistance - tau) < 0.01 && (coshdistance - tau) > -0.01) {
						for (int k=0; k<nedgepoints; k++) {
							double newt = p4d[i].t * (k+1)/(nedgepoints+1) + p4d[j].t * (nedgepoints-k)/(nedgepoints+1);
							double newx = p4d[i].x * (k+1)/(nedgepoints+1) + p4d[j].x * (nedgepoints-k)/(nedgepoints+1);
							double newy = p4d[i].y * (k+1)/(nedgepoints+1) + p4d[j].y * (nedgepoints-k)/(nedgepoints+1);
							double newz = p4d[i].z * (k+1)/(nedgepoints+1) + p4d[j].z * (nedgepoints-k)/(nedgepoints+1);
							//System.out.println("" + np4d);
							p4d[np4d] = new Point4D(newt,newx, newy, newz);

							np4d ++;
						}
					}
				}
			}
		}
		System.out.println("The number of vertices is: " + np4d);
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(tau)*1.1/(1+ nedgepoints) ) {
					//System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		
		System.out.println("The number of edges is: " + ne);
		*/
		
		
		
		//System.out.println("The number of vertices is: " + np4d);
		for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.atan(tau), 1, 0);
		//for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-acosh(coshedge));


		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point4D newp4d = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,p4d[i].z, curvature);
				newp4d.rotateTZ(acosh(coshedge));
				
				//double newx = p4d[i].x * Math.cos(Math.PI) + p4d[i].y * Math.sin(Math.PI);
				//double newy = -p4d[i].x * Math.sin(Math.PI) + p4d[i].y * Math.cos(Math.PI);
				
				p4d[i].x *= -1;
				p4d[i].y *= -1;
				
				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - newp4d.x) * (p4d[j].x - newp4d.x) +
							(p4d[j].y - newp4d.y) * (p4d[j].y - newp4d.y) +
							(p4d[j].z - newp4d.z) * (p4d[j].z - newp4d.z);
					if (distance < 0.01) {
						ignore = true;
						break;
					}
				}
				
				if (!ignore) {
					p4d[np4d] = new Point4D(newp4d.t,newp4d.x,newp4d.y,newp4d.z, curvature);
					np4d ++;
				}
			}
		}

		//System.out.println("The number of vertices is: " + np4d);
		
		
		/*
		for (int k = 1; k<=4; k++) {
			int axisindex = 20;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z);
					np4d ++;
				}
			}
		}
		
		/*
		for (int k = 1; k<=4; k++) {
			int axisindex = 191;
			Point3D axis = new Point3D(p4d[axisindex].x,p4d[axisindex].y,p4d[axisindex].z);
			//System.out.println("" + axis.x +" " + axis.y +" " + axis.z);
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				Point3D p3d = new Point3D(p4d[i].x,p4d[i].y,p4d[i].z);
				p3d.rotate3D(axis, Math.PI*2/5);

				boolean ignore = false;
				for (int j=0; j<currentnp; j++) {
					double distance = (p4d[j].x - p3d.x) * (p4d[j].x - p3d.x) +
							(p4d[j].y - p3d.y) * (p4d[j].y - p3d.y) +
							(p4d[j].z - p3d.z) * (p4d[j].z - p3d.z);
					if (distance < 0.1) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					p4d[np4d] = new Point4D(p4d[i].t,p3d.x,p3d.y,p3d.z);
					np4d ++;
				}
			}
		}
		*/
		/*
		{
			int currentnp = np4d;
			for (int i = 0; i< currentnp-1; i++ ) {
				for (int j = i+1; j< currentnp; j++ ) {
					double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
					//System.out.println(" "+ i + " " + j + " " +distance);
					if ( (coshdistance - coshedge) < 0.01 && (coshdistance - coshedge) > -0.01) {
						for (int k=0; k<nedgepoints; k++) {
							double newt = p4d[i].t * (k+1)/(nedgepoints+1) + p4d[j].t * (nedgepoints-k)/(nedgepoints+1);
							double newx = p4d[i].x * (k+1)/(nedgepoints+1) + p4d[j].x * (nedgepoints-k)/(nedgepoints+1);
							double newy = p4d[i].y * (k+1)/(nedgepoints+1) + p4d[j].y * (nedgepoints-k)/(nedgepoints+1);
							double newz = p4d[i].z * (k+1)/(nedgepoints+1) + p4d[j].z * (nedgepoints-k)/(nedgepoints+1);
							//System.out.println("" + np4d);
							p4d[np4d] = new Point4D(newt,newx, newy, newz);

							np4d ++;
						}
					}
				}
			}
		}
		*/
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//if (i==2) System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		
		
		//System.out.println(" " + p4d[2].x +" " + p4d[2].y +" " + p4d[2].z);
		//System.out.println("The number of vertices is: " + np4d);
		//System.out.println("The number of edges is: " + ne);
		
		
		
		{
			//for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.6, 1, 0);
			
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.3, -1, 0.7);
		}
		
		
	}
	
	public void init534 ( ) {
		coshedge = tau;
		np4d = 0;

		// layer 1
		p4d[np4d++] = new Point4D(1/tau12,0,0,0, curvature);//0
		p4d[np4d++] = new Point4D(tau12,1,0,0, curvature);
		p4d[np4d++] = new Point4D(tau32,tau,1,0, curvature);
		p4d[np4d++] = new Point4D(tau52,tau2, tau, 1, curvature);
		p4d[np4d++] = new Point4D(2*tau32,tau,ttau,ttau, curvature);
		p4d[np4d++] = new Point4D(Math.sqrt(5.0)*tau32,ttau,ttau,ttau, curvature);
		
		// layer 2
		p4d[np4d++] = new Point4D(tau52, 2* tau,0,0, curvature);//6
		p4d[np4d++] = new Point4D(tau72,2*ttau,1,0, curvature);
		p4d[np4d++] = new Point4D(Math.sqrt(5.0)*tau32,ttau*tau,tau,0, curvature);
		p4d[np4d++] = new Point4D(tau92,2*ttau*tau,tau,1, curvature);
		p4d[np4d++] = new Point4D(2*tau52,ttau*tau+tau,ttau,tau, curvature);
		p4d[np4d++] = new Point4D(Math.sqrt(5.0)*tau52,tau4,tau2,1, curvature);
		p4d[np4d++] = new Point4D(3 * tau52, 4 * tau + 3, ttau,tau, curvature);
		p4d[np4d++] = new Point4D(tau92,3*ttau,ttau,ttau, curvature);
		p4d[np4d++] = new Point4D(2*tau72, 5*tau +2, ttau,ttau, curvature);

		// layer 3
		p4d[np4d++] = new Point4D(tau72, tau3, 2* tau,0, curvature);//15
		p4d[np4d++] = new Point4D(tau92, tau4, 2*ttau, 1, curvature);
		p4d[np4d++] = new Point4D(tau72 + tau32, 0, 2*ttau, 2 * ttau, curvature);
		p4d[np4d++] = new Point4D(Math.sqrt(5.0)*tau52, tau3 + tau, tau3, tau, curvature);
		p4d[np4d++] = new Point4D(tau92 * tau - tau12, Math.sqrt(5.0) * tau3, 2*ttau*tau,tau, curvature);
		p4d[np4d++] = new Point4D(tau92 * tau - tau32, 1, 2*ttau*tau, 2*ttau*tau, curvature);
		p4d[np4d++] = new Point4D(tau92, ttau, tau3+tau, tau3+tau, curvature); //21
		p4d[np4d++] = new Point4D(2 * tau72, 3 * tau2, tau4, ttau, curvature); //22
		p4d[np4d++] = new Point4D(tau * tau92, tau5-1,  tau4 + ttau, ttau, curvature);//23

		// layer 4
		p4d[np4d++] = new Point4D(tau92, tau4, 2* tau, tau3, curvature);
		p4d[np4d++] = new Point4D(tau92 * tau - tau12, tau4 + ttau, tau4, 2*ttau, curvature);
		p4d[np4d++] = new Point4D(Math.sqrt(5.0) * tau72, Math.sqrt(5.0) * tau3, 2 * ttau, 2 * ttau, curvature);
		p4d[np4d++] = new Point4D(3 * tau52, tau4, tau3, Math.sqrt(5.0)* tau2,  curvature);
		p4d[np4d++] = new Point4D(tau92 * ttau - 2 * tau32, 2*tau4, Math.sqrt(5.0) * tau3, 2*tau3, curvature);
		p4d[np4d++] = new Point4D(tau92 * tau + 2 * tau32, 2*tau4,  2*tau3, 2*tau3, curvature);
		p4d[np4d++] = new Point4D(2*tau72, tau4, tau3+tau, tau3+tau, curvature);
		p4d[np4d++] = new Point4D(tau92 * tau, Math.sqrt(5.0)*tau3, 3*ttau, tau4, curvature);
		p4d[np4d++] = new Point4D(tau92 * tau + tau72,  2*tau4, tau5-1,  Math.sqrt(5.0)*tau3, curvature);

		// outer layer
//		p4d[np4d++] = new Point4D(tau92 * tau + tau12, tau5 + tau, 2*ttau*tau,tau, curvature);
//		p4d[np4d++] = new Point4D(tau92 * tau + tau32, 2*tau4,1, 2*ttau*tau, curvature);
//		p4d[np4d++] = new Point4D(tau52 + 3* tau32, 2*tau3, tau3,0, curvature);
//		p4d[np4d++] = new Point4D(3*tau72 - tau52, tau5 + tau, tau,0, curvature);
//		p4d[np4d++] = new Point4D(tau92 + tau12, tau4 + tau, 2 * ttau, 0, curvature);
//		p4d[np4d++] = new Point4D(tau52 * (ttau + 2), ttau * ttau * tau + ttau * tau, 1, 0, curvature);
//		p4d[np4d++] = new Point4D(tau72 + 2*tau32,4*tau+3,0,0, curvature);
		
//		System.out.println(np4d);
		
//		 generate new points by cycling
		{
			int currentnp = np4d;
			for (int i = 0; i<currentnp; i++) {
				if (Math.abs(p4d[i].y - p4d[i].z) < 1e-10) {
					if (Math.abs(p4d[i].x - p4d[i].y) < 1e-10) {
						// all same. do nothing
					} else {
						// even perm
						p4d[np4d++] = new Point4D(p4d[i].t,p4d[i].y,p4d[i].z,p4d[i].x, curvature);
						p4d[np4d++] = new Point4D(p4d[i].t,p4d[i].z,p4d[i].x,p4d[i].y, curvature);
					}
				} else {
					// all different, all perms
					p4d[np4d++] = new Point4D(p4d[i].t,p4d[i].y,p4d[i].z,p4d[i].x, curvature);
					p4d[np4d++] = new Point4D(p4d[i].t,p4d[i].z,p4d[i].x,p4d[i].y, curvature);
					p4d[np4d++] = new Point4D(p4d[i].t,p4d[i].z,p4d[i].y,p4d[i].x, curvature);
					p4d[np4d++] = new Point4D(p4d[i].t,p4d[i].x,p4d[i].z,p4d[i].y, curvature);
					p4d[np4d++] = new Point4D(p4d[i].t,p4d[i].y,p4d[i].x,p4d[i].z, curvature);
				}
			}
		}
		
		////// generate new points by mirroring
//		{
//			int currentnp = np4d;
//			for (int i = 0; i<currentnp; i++) {
//				if (p4d[i].x > 1e-10) {
//					p4d[np4d] = new Point4D(p4d[i].t,-p4d[i].x,p4d[i].y,p4d[i].z, curvature);
//					np4d ++;
//				}
//			}
//		}
//		
//		
//		{
//			int currentnp = np4d;
//			for (int i = 0; i<currentnp; i++) {
//				if (p4d[i].y > 1e-10) {
//					p4d[np4d] = new Point4D(p4d[i].t,p4d[i].x,-p4d[i].y,p4d[i].z, curvature);
//					np4d ++;
//				}
//			}
//		}
//		
//		{
//			int currentnp = np4d;
//			for (int i = 0; i<currentnp; i++) {
//				if (p4d[i].z > 1e-10) {
//					p4d[np4d] = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,-p4d[i].z, curvature);
//					np4d ++;
//				}
//			}
//		}
//
//		
//		{
//			int currentnp = np4d;
//			
//			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(acosh(tau));
//			
//			for (int i = 0; i<currentnp; i++) {
//				
//				if (p4d[i].z > 1e-10) {
//					p4d[np4d] = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,-p4d[i].z, curvature);
//					np4d ++;
//				}
//			}
//		}
		
		/*
		{
			
			int currentnp = np4d;
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2,1,0);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(acosh(tau));
			
			for (int i = 0; i<currentnp; i++) {
				
				if (p4d[i].z > 1e-10) {
					p4d[np4d] = new Point4D(p4d[i].t,p4d[i].x,p4d[i].y,-p4d[i].z);
					np4d ++;
				}
			}
		}
		*/
		
		//System.out.println("The number of vertices is: " + np4d);

		/*
		int index = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( (coshdistance - tau) < 0.01 && (coshdistance - tau) > -0.01) {
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}*/

		/*		
		{
			int currentnp = np4d;
			for (int i = 0; i< currentnp-1; i++ ) {
				for (int j = i+1; j< currentnp; j++ ) {
					double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
					//System.out.println(" "+ i + " " + j + " " +distance);
					if ( (coshdistance - coshedge) < 0.01 && (coshdistance - coshedge) > -0.01) {
						for (int k=0; k<nedgepoints; k++) {
							double newt = p4d[i].t * (k+1)/(nedgepoints+1) + p4d[j].t * (nedgepoints-k)/(nedgepoints+1);
							double newx = p4d[i].x * (k+1)/(nedgepoints+1) + p4d[j].x * (nedgepoints-k)/(nedgepoints+1);
							double newy = p4d[i].y * (k+1)/(nedgepoints+1) + p4d[j].y * (nedgepoints-k)/(nedgepoints+1);
							double newz = p4d[i].z * (k+1)/(nedgepoints+1) + p4d[j].z * (nedgepoints-k)/(nedgepoints+1);
							//System.out.println("" + np4d);
							p4d[np4d] = new Point4D(newt,newx, newy, newz);

							np4d ++;
						}
					}
				}
			}
		}
		System.out.println("The number of vertices is: " + np4d);
		*/
		
		int index = 0;
		ne = 0;
		for (int i = 0; i< np4d-1; i++ ) {
			for (int j = i+1; j< np4d; j++ ) {
				double coshdistance = p4d[i].t * p4d[j].t - p4d[i].x * p4d[j].x - p4d[i].y * p4d[j].y  - p4d[i].z * p4d[j].z;  
				//System.out.println(" "+ i + " " + j + " " +distance);
				if ( acosh(coshdistance) < acosh(coshedge)*1.1/(1+ nedgepoints) ) {
					//System.out.println(" "+ i + " " + j + " " +index);
					edges[index] = new Edge(i,j);
					index ++;
					ne ++;
				}
			}
		}
		
		
		System.out.println("The number of edges is: " + ne);
		
		{
			//for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.6, 1, 0);
			
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.5);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(-Math.PI/2, 1/Math.sqrt(2), -1/Math.sqrt(2));
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(-0.1);
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(0.3, -1, 0.7);
		}
	}
	
	public double acosh (double z) {
		return Math.log(z + Math.sqrt((z+1)*(z-1)));						
	}

	public void drawPuzzle (Graphics g) {

		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.blue);
		Graphics2D g2d = (Graphics2D)g;
		
		if (ne < 500)		{
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		Font font = new Font("Arial", Font.PLAIN, 22);
		g2d.setFont(font);
		
//		for (int i = 0; i<np4d; i++) {
//			if (p4d[i].z > 0 || curvature > 0)	g.fillOval((int)(center.x + p4d[i].x2d*scaleScreen - scalePoint*p4d[i].pointsize/2), (int)(center.y - p4d[i].y2d*scaleScreen- scalePoint*p4d[i].pointsize/2),	(int)(scalePoint*p4d[i].pointsize), (int)(scalePoint*p4d[i].pointsize));
//			if (p4d[i].z > 0) 	g.drawString(""+i,(int)(center.x + p4d[i].x2d*scaleScreen - scalePoint*p4d[i].pointsize/2),	(int)(center.y - p4d[i].y2d*scaleScreen- scalePoint*p4d[i].pointsize/2));
//		}
		
//		if (true) return;
		
		g.setColor(Color.gray);

		int[][] edgecenters;
		edgecenters = new int[ne][2];
		
		for (int k=0; k < ne; k++) {
			float edgecolor=(float)(0.);
			int i = edges[k].p1;
			int j = edges[k].p2;
			
			if (p4d[i].z<0 && p4d[j].z<0) {
				//if (CB333.getState()) {		edgecolor = (float)(2/(p4d[j].dist+p4d[i].dist));	} else {	edgecolor = (float)(0);		}				
				edgecolor = (float)(0);
			} else if (p4d[i].z<0) {
				edgecolor = (float)(1/p4d[j].dist);
			} else if (p4d[j].z<0) {
				edgecolor = (float)(1/p4d[i].dist);
			}
			else {
				edgecolor = (float)(2/(p4d[j].dist+p4d[i].dist));
			}
			edgecenters[k][0] = (int)(1000000*(edgecolor));
			edgecenters[k][1] = k;
		}

		
		Arrays.sort(edgecenters, new Comparator<int[]>() {
			//@Override
			public int compare(int[] o1, int[] o2) {
				return -((Integer) o2[0]).compareTo(o1[0]);
			}
		});
		
		
		//int margin = 200;
		
		double nf = -100;
		
		//int counter = 0;
		//int counter0 = 0;
		//int counter1 = 0;

		for (int kk = 0; kk< ne; kk++) {
			int k = edgecenters[kk][1];
			int i = edges[k].p1;
			int j = edges[k].p2;
			//if (p4d[i].z<0 && p4d[j].z<0 && (!CB333.getState())) continue;
			if (p4d[i].z<0 && p4d[j].z<0 ) continue;

			//if (CB335.getState() && (p4d[i].t<0 && p4d[j].t<0)) continue;
			//if ((p4d[i].t<0 && p4d[j].t<0)) continue;

			int x1, y1, x2, y2;
			double avgpointsize;
			float edgecolor = 0;
			if (p4d[i].z<0) {
				x1 = (int)(center.x + p4d[j].x2d*scaleScreen + nf*(p4d[i].x2d-p4d[j].x2d)*scaleScreen);
				y1 = (int)(center.y - p4d[j].y2d*scaleScreen - nf*(p4d[i].y2d-p4d[j].y2d)*scaleScreen);
				x2 = (int)(center.x + p4d[j].x2d*scaleScreen);
				y2 = (int)(center.y - p4d[j].y2d*scaleScreen);		
				avgpointsize = (p4d[j].pointsize);
				edgecolor = (float)(1/p4d[j].dist);
			} else if (p4d[j].z<0) {
				x1 = (int)(center.x + p4d[i].x2d*scaleScreen + nf*(p4d[j].x2d-p4d[i].x2d)*scaleScreen);
				y1 = (int)(center.y - p4d[i].y2d*scaleScreen - nf*(p4d[j].y2d-p4d[i].y2d)*scaleScreen);
				x2 = (int)(center.x + p4d[i].x2d*scaleScreen);
				y2 = (int)(center.y - p4d[i].y2d*scaleScreen);		
				avgpointsize = (p4d[i].pointsize);	
				edgecolor = (float)(1/p4d[i].dist);
			}
			else {
				x1 = (int)(center.x + p4d[i].x2d*scaleScreen);
				y1 = (int)(center.y - p4d[i].y2d*scaleScreen);
				x2 = (int)(center.x + p4d[j].x2d*scaleScreen);
				y2 = (int)(center.y - p4d[j].y2d*scaleScreen);
				avgpointsize = (p4d[i].pointsize + p4d[j].pointsize)/2;
				edgecolor = (float)(2/(p4d[j].dist+p4d[i].dist));
			}


//			if ( (x1<0 -margin || x1>width +margin|| y1<0-margin || y1>height+margin) && (x2<0-margin || x2>width+margin || y2<0-margin || y2>height+margin)) {
			//	counter1 ++;
			//	continue;				
//			} else {
				//counter0 ++;
//			}

			//float edgecolor = (float)(avgpointsize/4);
			edgecolor /=1.7;
			if (curvature > 0) edgecolor = (float)(Math.pow(edgecolor, 1.4));
			//if (CB335.getState() || CB533.getState()) edgecolor= (float)(Math.pow(edgecolor, 1.2));
			if (edgecolor>1) {
				edgecolor = 1;
			}
			if (ne >= 500) {
				if (edgecolor > 0.75) {
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					//counter++;
				} 

			}

			g2d.setStroke(new BasicStroke((int)(avgpointsize*2)));
			if ((int)(avgpointsize*2)<1) g2d.setStroke(new BasicStroke(1));
			g.setColor(new Color(edgecolor,edgecolor,edgecolor));
			//if ( (x1<0 -margin || x1>width +margin|| y1<0-margin || y1>height+margin) && (x2<0-margin || x2>width+margin || y2<0-margin || y2>height+margin)) continue;
			//if (kk%4 != 0) continue;
			
			g.drawLine(x1, y1, x2, y2);
		}
		//System.out.println(((float)(counter1)/(counter0 + counter1)));
		
		//g2d.setStroke(new BasicStroke(1));
		//g.setColor(new Color(0,200,0));
		//g.drawOval(center.x - (int)scaleScreen/2, center.y - (int)scaleScreen/2, (int)scaleScreen, (int)scaleScreen);
	}
	
	public void mouseMoved( MouseEvent e ) {
		mx = e.getX();
		my = e.getY();
//		drawPuzzle( backg );
//		repaint();
		e.consume();
	}
	public void mouseDragged( MouseEvent e ) { 
		int new_mx = e.getX();
		int new_my = e.getY();

		// adjust angles according to the distance travelled by the mouse
		// since the last event
		double dx = (double)(new_mx - mx)*rotationspeed;
		double dy = -(double)(new_my - my)*rotationspeed;
		double dz = Math.sqrt(dx*dx + dy * dy);
		double ux = 0;
		double uux = 0;
		double uy = 0;
		if (dz>0) {
			ux = -dy/dz;
			uux = ux;
			uy = dx/dz;
		}
		double theta = dz;

		if (e.isShiftDown()) {
			theta *= uux;
			//if (dy > 0 ) theta = -theta;
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateTZ(theta/5);
		} else {
			for (int i = 0; i< np4d; i++) 	p4d[i].rotateXYZ(theta/3, ux, uy);
		}

		//System.out.println("" + p4d[1].z);
		
		drawPuzzle( backg );
		//drawEdges(backg);

		// update our data
		mx = new_mx;
		my = new_my;

		repaint();
		e.consume();
	}

	public void update( Graphics g ) {
		g.drawImage( backbuffer, 0, 0, this );
	}

	public void paint( Graphics g ) {
		update( g );
	}

	public void itemStateChanged(ItemEvent arg0) {
		initpuzzle();
		drawPuzzle( backg );
		repaint();
	}

	public void componentHidden(ComponentEvent arg0) {
		
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentResized(ComponentEvent arg0) {
		width = getSize().width;
		height = getSize().height;
		center.x = width/2;
		center.y = height/2;
		
//		center = new Point(width/2,height/2);
		scaleScreen = (double)((width+height)/12);
		CBs.setBounds(width - 250, 0, 250, 25*11);
		CBmisc.setBounds(width - 125, 25*11+1, 125, 7*25);
		
		intro.setBounds(width - 150, height - 40, 150, 40);
		//initpuzzle();
		backbuffer = createImage( width, height );
		backg = backbuffer.getGraphics();
		drawPuzzle( backg );
		repaint();
	}

	public void componentShown(ComponentEvent arg0) {
		
	}
}