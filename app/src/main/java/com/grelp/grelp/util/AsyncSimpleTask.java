package com.grelp.grelp.util;

import android.os.AsyncTask;

public class AsyncSimpleTask extends AsyncTask<Void, Void, Void> {
    private AsyncSimpleTask.AsyncSimpleTaskHandler handler;

    public AsyncSimpleTask(AsyncSimpleTask.AsyncSimpleTaskHandler handler) {
        this.handler = handler;
        super.execute(new Void[0]);
    }

    protected Void doInBackground(Void... voids) {
        this.handler.doInBackground();
        return null;
    }

    protected void onPostExecute(Void arg) {
        this.handler.onPostExecute();
    }

    public abstract static class AsyncSimpleTaskHandler {
        public AsyncSimpleTaskHandler() {
        }

        public abstract void doInBackground();

        public void onPostExecute() {
        }
    }
}
