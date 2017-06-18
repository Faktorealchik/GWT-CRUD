package com.app.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.app.client.builder.pages.EntityPage;

public class App implements EntryPoint {

    /**
     * точка входа
     */
    public void onModuleLoad() {
        RootPanel.get().add(new EntityPage());
    }
}