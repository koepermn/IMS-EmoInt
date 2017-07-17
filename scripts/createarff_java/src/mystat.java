


import java.io.DataInputStream;
import java.io.IOException;
import java.lang.Integer;
import java.lang.Float;
import java.lang.Double;
import java.lang.Math;

/**
 *  <b>Parametric Analysis of a Data Distribution</b>
 *  <p>
 *  This program will calculate the first four moments
 *  of a data distribution: average, standard deviation,
 *  skew and kurtosis of a given set of data.
 *  <p>
 *  See the tst class sample usage at the bottom of this file.
 *  <p>
 *  <b>$Id$</b>
 *  <p>
 * @author John Huddleston <a href=jhudd@weru.ksu.edu>jhudd@weru.ksu.edu</a>
 *  <p>
 * @version $Revision$
 *  <p>
 */

public class mystat {

	public void mystat()
	{
	}

/**
 *  Given an input stream of data in the data vector, and the number
 *  number of data elements in the ncnt integer,
 *  the first, second, third and fourth moments of the input data stream,
 *  average, standard deviation, skewness and kurtosis, are
 *  calculated and placed in their respective 1D vectors;
 *
 *  @param ncnt is the input number of data
 *  @param data is the input vector of data
 *  @param avg is the output arithmetic mean of the data
 *  @param stdev is the output second moment of the data
 *  @param skew is the output third moment of the data
 *  @param kurtosis is the output fourth moment of the data
 *  @return none
 *  @see Math#sqrt
 */
	public void mystat(int ncnt,double[] data,double[] avg,double[] stdev,double[] skew,double[] kurtosis)
	{
		double var,d,v1,v2,v3,v4; // temporary variables
		int i;
//ncnt = data lenght
		v1 = v2 = v3 = v4 = 0.0;
		stdev[0] = 0.0;
		skew[0] = 0.0;
		kurtosis[0] = 0.0;
		if(ncnt < 3)	return;
		for(i=0;i<ncnt;i++)
			v1 += data[i];
		avg[0] = v1/ncnt;
		for(i=0;i<ncnt;i++)
		{
			d = data[i] - avg[0];
			v2 = v2 + d*d;
			v3 = v3 + d*d*d;
			v4 = v4 + d*d*d*d;
		}
		var = v2/(ncnt-1);
		stdev[0] = Math.sqrt(var);

		// standard skewness for univariate data
		skew[0] = (ncnt*v3)/((ncnt-1)*(ncnt-2));
		skew[0] /= (stdev[0]*stdev[0]*stdev[0]);

		kurtosis[0] = (ncnt*ncnt*v4)/((ncnt-1)*(ncnt-2)*(ncnt-3));
		kurtosis[0] = kurtosis[0]/(var*var);
	}

/**
 *  Given an input data set the standard moment skewness and kurtosis
 *  coefficients are calculated
 *
 *  @param data is the input data
 *  @param skew is the output third moment of the data
 *  @param kurtosis is the output fourth moment of the data
 *  @return none
 */
	public void moment_skewness_kurtosis(double[] data, double[] skew,double[] kurtosis)
	{
	int ncnt, i;
	double sum1 = 0.0, sum2 = 0.0, sum3 = 0.0, sum4 = 0.0;
	double rm1, rm2, rm3, rm4;
	double cm1, cm2, cm3, cm4;
	double b1, b2;

	ncnt = data.length;
	for(i = 0; i < ncnt; i++)
	{
		sum1 = sum1 + data[i];
		sum2 = sum2 + data[i] * data[i];
		sum3 = sum3 + data[i] * data[i] * data[i];
		sum4 = sum4 + data[i] * data[i] * data[i] * data[i];
	}

	rm1 = sum1 / ncnt;
	rm2 = sum2 / ncnt;
	rm3 = sum3 / ncnt;
	rm4 = sum4 / ncnt;

	cm1 = rm1 - rm1;
	cm2 = rm2 - (rm1 * rm1);
	cm3 = rm3 - (3 * rm2 * rm1) + (2 * (rm1 * rm1 *rm1));
	cm4 = rm4 - (4 * rm3 * rm1) + (6 * rm2 * rm1 * rm1) - (3 * rm1 * rm1 * rm1 *rm1);

	b1 = (cm3 *cm3) / (cm2 * cm2 * cm2);
	b2 = cm4 / (cm2 * cm2);
	skew[0] = b1;
	kurtosis[0] = b2;

	}
/**
 *  Using pearson skew, outliers have strong influence
 *
 *  @param data is the input data
 *  @param skew1 is the output Pearson's 1st Measure of Skewness
 *  @param skew2 is the output Pearson's 2nd Measure of Skewness
 *  @return none
 */
	public void pearson_skew(double[] data, double[] skew1,double[] skew2)
	{
	int ncnt, i, j;
	int m, n;
	double mean, median, mode;
	double var, sd;
	double sum1 = 0.0, sum2 = 0.0;
	double temp;

	ncnt = data.length;
	for(i = 0; i < ncnt; i++)
	{
		sum1 = sum1 + data[i];
		sum2 = sum2 + (data[i] * data[i]);
	}

	mean = sum1 / ncnt;
	var = (sum2 / ncnt) - (mean * mean);
	sd = Math.sqrt(var);

	for(i = 0; i < ncnt - 1; i++)
	{
		for(j = i + 1; j < ncnt; j++)
		{
			if(data[i] > data[j])
			{
				temp = data[i];
				data[i] = data[j];
				data[j] = temp;
			}
		}
	}

	if(ncnt % 2 == 0)
	{
		m = ncnt / 2;
		median = (data[m - 1] + data[m]) / 2;
	}
	else
	{
		n = ncnt / 2;
		median = data[n];
	}
	mode = (3.0 * mean) - (2.0 * median);
	skew1[0] = (mean - mode) / sd; //Pearson's 1st Measure of Skewness
	skew2[0] = (3 * (mean - median)) / sd; //Pearson's 2nd Measure of Skewness
	}

/**
 *  Given an input data vector compute the skewness based on Bowley's
 *
 *  @param data is the input data vactor
 *  @param skew is the output third moment of the data
 *  @return none
 */
	public void bowley_skew(double[] x, double[] skew)
	{
	int m, n, i, j, p;
	double q1, q2, q3, temp;

	p = x.length;
	for(i = 0; i <= p - 2; i++)
	{
		for(j = i + 1; j <= p - 1; j++)
		{
			if(x[i] > x[j])
			{
				temp = x[i];
				x[i] = x[j];
				x[j] = temp;
			}
		}
	}

	if(p % 2 == 0)
	{
		m = (p + 1) % 4;
		q1 = (x[m + 1] + x[m]) / 2;
	}
	else
	{
		n = (p + 1) / 4;
		q1 = x[n];
	}

	if(p % 2 == 0)
	{
		m = p / 2;
		q2 = (x[m - 1] + x[m]) / 2;
	}
	else
	{
		n = p / 2;
		q2 = x[n];
	}

	if(p % 2 == 0)
	{
		m = ((p + 1) % 4) * 3;
		q3 = (x[p - m] + x[p - m + 1]) / 2;
	}
	else
	{
		n = ((p + 1) / 4) * 3;
		q3 = x[n];
	}

	skew[0] = (q3 - q2 - q2 + q1) / (2 * (q3 - q2 + q2 - q1));

	}


	public static void main(String args[])
	{
	int ncnt,iret;
	int m, i;
	double[] avg = new double[1];
	double[] std = new double[1];
	double[] skw = new double[1];
	double[] sk2 = new double[1];
	double[] kur = new double[1];

	//
	// Station : 8976, TALKEETNA WSCMO AP
	// -------   Unit = degrees F
	// Years 50 to 90 January data
	double[] data = {
		-14.00, -4.00, -9.00, -2.00, -3.00, 7.00, -2.00, -1.00, 8.00, 2.00,
		6.00, 11.00, 0.00, 11.00, 2.00, -2.00, -2.00, -9.00, -3.00, -11.00,
		-6.00, -20.00, -12.00, -14.00, -10.00, -5.00, -1.00, 22.00, 11.00,
		6.00, -2.00, 23.00, -8.00, 4.00, 11.00, 5.00, -17.00
	};

	/*
	if(args.length > 0)
	{
		System.out.println("Usage: cd ../../..");
		System.out.println("       javac usda/weru/gener/mystat");
		System.out.println("       java -cp . usda.weru.gener.tstmystat");
		return;
	}*/

	// instantiate mystat
	mystat tst = new mystat();

	avg[0]=std[0]=skw[0]=kur[0]=0.0;
	ncnt = data.length;
	tst.mystat(ncnt,data,avg,std,skw,kur);

	System.out.println("      MyStat Statistics Analysis");
	System.out.println("average = " + avg[0]);
	System.out.println("standard deviation = " + std[0]);
	System.out.println("coefficient of skewness = " + skw[0]);
	System.out.println("coefficient of kurtosis = " + kur[0]);

	tst.moment_skewness_kurtosis(data,skw,kur);
	System.out.println("      Moment Statistics Analysis");
	System.out.println("skewness = " + skw[0]);
	System.out.println("kurtosis = " + kur[0]);

	tst.pearson_skew(data,skw,sk2);
	System.out.println("      Pearson Statistics Analysis");
	System.out.println("Pearson's 1st Measure of Skewness = " + skw[0]);
	System.out.println("Pearson's 2nd Measure of Skewness = " + sk2[0]);

	tst.bowley_skew(data,skw);
	System.out.println("      Bowley Statistics Analysis");
	System.out.println("Bowley's Measure of Skewness = " + skw[0]);
	System.out.flush();
	}
	

    public static float calculateSkewForSampleArray(float[] arr) {
        if (arr == null || arr.length == 0) {
            return 0f;
        }
        float mean = 0f;
        for (int i = 0; i < arr.length; i++) {
            mean += arr[i];
        }
        mean /= (float) arr.length;
        float m3 = 0;
        float m2 = 0;
        float tmpSquare;
        float tmpVal;
        for (int i = 0; i < arr.length; i++) {
            tmpVal = arr[i] - mean;
            tmpSquare = tmpVal * tmpVal;
            m2 += tmpSquare;
            m3 += tmpSquare * tmpVal;
        }
        m2 /= (float) arr.length;
        m3 /= (float) arr.length;
        float skew = m3 / (float) Math.pow(m2, 1.5f);
        return skew;
    }

    /**
     * Calculates the third standard moment of the provided value array.
     *
     * @param arr Integer value array.
     * @return Skewness of the specified distribution.
     */
    public static float calculateSkewForSampleArray(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0f;
        }
        float mean = 0f;
        for (int i = 0; i < arr.length; i++) {
            mean += arr[i];
        }
        mean /= (float) arr.length;
        float m3 = 0;
        float m2 = 0;
        float tmpSquare;
        float tmpVal;
        for (int i = 0; i < arr.length; i++) {
            tmpVal = arr[i] - mean;
            tmpSquare = tmpVal * tmpVal;
            m2 += tmpSquare;
            m3 += tmpSquare * tmpVal;
        }
        m2 /= (float) arr.length;
        m3 /= (float) arr.length;
        float skew = m3 / (float) Math.pow(m2, 1.5f);
        return skew;
    }

    

    /**
     * Calculates the fourth standard moment of the provided value array.
     *
     * @param arr Float value array.
     * @return Kurtosis of the specified distribution.
     */
    public static float calculateKurtosisForSampleArray(float[] arr) {
        if (arr == null || arr.length == 0) {
            return 0f;
        }
        float mean = 0f;
        for (int i = 0; i < arr.length; i++) {
            mean += arr[i];
        }
        mean /= (float) arr.length;
        float m4 = 0;
        float m2 = 0;
        float tmpSquare;
        float tmpVal;
        for (int i = 0; i < arr.length; i++) {
            tmpVal = arr[i] - mean;
            tmpSquare = tmpVal * tmpVal;
            m2 += tmpSquare;
            m4 += tmpSquare * tmpSquare;
        }
        m2 /= (float) arr.length;
        m4 /= (float) arr.length;
        float kurtosis = (m4 / (float) Math.pow(m2, 2)) - 3;
        return kurtosis;
    }

    /**
     * Calculates the fourth standard moment of the provided value array.
     *
     * @param arr Integer value array.
     * @return Kurtosis of the specified distribution.
     */
    public static float calculateKurtosisForSampleArray(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0f;
        }
        float mean = 0f;
        for (int i = 0; i < arr.length; i++) {
            mean += arr[i];
        }
        mean /= (float) arr.length;
        float m4 = 0;
        float m2 = 0;
        float tmpSquare;
        float tmpVal;
        for (int i = 0; i < arr.length; i++) {
            tmpVal = arr[i] - mean;
            tmpSquare = tmpVal * tmpVal;
            m2 += tmpSquare;
            m4 += tmpSquare * tmpSquare;
        }
        m2 /= (float) arr.length;
        m4 /= (float) arr.length;
        float kurtosis = (m4 / (float) Math.pow(m2, 2)) - 3;
        return kurtosis;
    }
    
}

