package org.gs4tr.termmanager.webservice.controllers;

import java.util.Arrays;

public class Test {

	public void foo(String s) {
		System.out.println("String");
	}

	public void foo(StringBuffer sb){
		System.out.println("StringBuffer");
	}

    public static void main(String[] args) throws InterruptedException {




		    new Test().foo(null);

    }

    private static int birthdayCakeCandles(int[] arr) {

	int max = arr[0];
	int counter = 0;

	for (int x : arr) {
	    if (x > max) {
		max = x;
	    }
	}

	for (int x : arr) {
	    if (x == max) {
		counter++;
	    }
	}
	return counter;
    }

    private static void countApplesAndOranges(int s, int t, int a, int b, int[] apples, int[] oranges) {

	int nApples = 0;
	int nOranges = 0;

	for (int x : apples) {
	    int y = x + a;
	    if (y >= s && y <= t) {
		nApples++;
	    }
	}
	for (int x : oranges) {
	    int y = x + b;
	    if (y >= s && y <= t) {
		nOranges++;
	    }
	}

	System.out.println(nApples);
	System.out.println(nOranges);

    }

    private static boolean isPalindrome(String s) {

	String a = s.replaceAll("\\s+", "").toLowerCase();

	for (int i = 0; i <= a.length() / 2; i++) {

	    char first = a.charAt(i);
	    char last = a.charAt(a.length() - 1 - i);

	    if (first != last) {
		return false;
	    }

	}

	return true;

    }

    private static String kangaroo(int x1, int v1, int x2, int v2) {

	if (((x1 + v1) * x2) % v2 == 0) {
	    return "YES";
	}
	return "NO";
    }

    private static void sort(int[] arr) {

	int temp;

	for (int i = 1; i <= arr.length - 1; i++) {

	    for (int j = 1; j <= arr.length - 1; j++) {

		if (arr[i - 1] > arr[i]) {
		    temp = arr[i];
		    arr[i] = arr[i - 1];
		    arr[i - 1] = temp;
		}

	    }

	}

	System.out.println(Arrays.toString(arr));

    }

    private static void staircase(int n) {

	StringBuilder builder = new StringBuilder();

	int x = 0;

	while (n != 0) {
	    for (int i = n - 2; i >= 0; i--) {
		builder.append(" ");
	    }

	    for (int i = 0; i <= x; i++) {
		builder.append("#");
	    }

	    builder.append("\n");
	    x++;
	    n--;

	}

	System.out.println(builder.toString());

    }

    static void bubbleSort(int[] ar) {

	for (int i = 0; i <= ar.length - 1; i++) {

	    for (int j = 1; j <= ar.length - 1; j++) {

		if (ar[j - 1] > ar[j]) {
		    int temp = ar[j - 1];
		    ar[j - 1] = ar[j];
		    ar[j] = temp;
		}
	    }
	}
	System.out.println(Arrays.toString(ar));
    }

}
