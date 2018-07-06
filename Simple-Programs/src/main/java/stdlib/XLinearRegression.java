package stdlib;
public class XLinearRegression {
	private final int N;
	private final double beta0, beta1;
	private final double R2;
	private final double svar, svar0, svar1;

	public XLinearRegression(double[] x, double[] y) {
		N = x.length;

		// first pass
		double sumx = 0.0, sumy = 0.0; //, sumx2 = 0.0;
		for (int i = 0; i < N; i++) sumx  += x[i];
		//for (int i = 0; i < N; i++) sumx2 += x[i]*x[i];
		for (int i = 0; i < N; i++) sumy  += y[i];
		double xbar = sumx / N;
		double ybar = sumy / N;

		// second pass: compute summary statistics
		double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for (int i = 0; i < N; i++) {
			xxbar += (x[i] - xbar) * (x[i] - xbar);
			yybar += (y[i] - ybar) * (y[i] - ybar);
			xybar += (x[i] - xbar) * (y[i] - ybar);
		}
		beta1 = xybar / xxbar;
		beta0 = ybar - beta1 * xbar;

		// more statistical analysis
		double rss = 0.0;      // residual sum of squares
		double ssr = 0.0;      // regression sum of squares
		for (int i = 0; i < N; i++) {
			double fit = beta1*x[i] + beta0;
			rss += (fit - y[i]) * (fit - y[i]);
			ssr += (fit - ybar) * (fit - ybar);
		}

		int df = N-2;
		R2    = ssr / yybar;
		svar  = rss / df;
		svar1 = svar / xxbar;
		svar0 = svar/N + xbar*xbar*svar1;
	}

	// y = beta1*x + beta0
	// y = slope*x + intercept  [ rename to slope and intercept ]
	public double beta0() { return beta0; }
	public double beta1() { return beta1; }

	// R^2
	public double R2() { return R2; }

	// standard error of beta0 and beta1
	public double beta0StdErr() { return Math.sqrt(svar0); }
	public double beta1StdErr() { return Math.sqrt(svar1); }

	// predict a value of y, given a value of x
	public double predict(double x) {
		return beta1*x + beta0;
	}

	public String toString() {
		String s = "";
		s += String.format("%.2f N + ", beta1());
		s += String.format("%.2f ", beta0());
		return s + " (R^2 = " + String.format("%.3f", R2()) + ")";
	}


}

