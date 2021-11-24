package org.gs4tr.termmanager.webservice.controllers;

public class MyThread implements Thread {

    private boolean decrement;

    public MyThread(boolean decrement) {
	this.decrement = decrement;
    }

    @Override
    public void run() {

	if (decrement) {
	    for (int i = 10; i > 0; i--) {
		System.out.println(i);
		try {
		    Thread.sleep(500);
		} catch (InterruptedException ex) {
		}
	    }
	} else {
	    for (int i = 1; i < 11; i++) {
		System.out.print(i);
		try {
		    Thread.sleep(500);
		} catch (InterruptedException ex) {
		}
	    }
	}

    }

    public void setDecrement(boolean decrement) {
	this.decrement = decrement;
    }
}
