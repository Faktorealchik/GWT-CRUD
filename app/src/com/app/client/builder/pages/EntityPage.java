package com.app.client.builder.pages;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.app.client.builder.book.Book;
import com.app.client.builder.helper.Helper;

import java.util.LinkedList;
import java.util.List;

public class EntityPage extends Composite {

    @UiField(provided = true)
    DoubleClickTable table; //FlexTable

    @UiField
    TextBox name;
    @UiField
    TextBox author;
    @UiField
    TextBox price;
    @UiField
    TextBox count;

    @UiField
    Button addButton;
    @UiField
    Button deleteActive;
    @UiField
    VerticalPanel vertPanel;

    private Book currentBook;
    List<Book> books = new LinkedList<>();
    private Helper helper = new Helper();

    /**
     * В конструкторе устанавливаем таблицу,
     * добавляем слушателей ко всем кнопкам.
     */
    public EntityPage() {

        setupTable();
        initWidget(uiBinder.createAndBindUi(this));

        name.getElement().setAttribute("placeholder", "Name");
        author.getElement().setAttribute("placeholder", "Author");
        price.getElement().setAttribute("placeholder", "Price");
        count.getElement().setAttribute("placeholder", "Count");


        createDoubleClickHandler();
        table.addClickHandler(event -> {
            HTMLTable.Cell cell = table.getCellForEvent(event);
            int rowIndex = cell.getRowIndex();
            int cellIndex = cell.getCellIndex();

            CheckBox checkBox = (CheckBox) table.getWidget(rowIndex, 0);

            //если клинкнули на сам чекбокс(так как он меняется сам, то ничего не делаем
            //но, если нажимать на ячейку 0, рядом с ним, то меняться не будет тоже.
            if(cellIndex==0){
                return;
            }

            if (rowIndex > 0) {
                if (checkBox.getValue()) {
                    checkBox.setValue(false);
                } else {
                    checkBox.setValue(true);
                }
                table.setWidget(rowIndex, 0, checkBox);

            }
        });
        count.addKeyDownHandler(event ->
        {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                addRow();
            }
        });

        addButton.addClickHandler(event -> addRow());

        deleteActive.addClickHandler(event ->
        {
            for (int i = 1; i < table.getRowCount(); i++) {
                CheckBox checkBox = (CheckBox) table.getWidget(i, 0);
                if (checkBox.getValue()) {
                    String st = table.getText(i, 1);
                    table.removeRow(i);
                    Book b = new Book();
                    b.setName(st);
                    books.remove(b);
                    i--;
                }
            }
        });
    }


    /**
     * Устанавливаем слушателя на двойной клик
     * получаем строку, на которую кликнули
     * получаем книгу из строки
     * отсылаем запрос на обновление этой книги
     */
    private HandlerRegistration createDoubleClickHandler() {
        return table.addDoubleClickHandler(event -> {
            HTMLTable.Cell cell = table.getCellForEvent(event);
            int rowIndex = cell.getRowIndex();

            String name = table.getText(rowIndex, 1);
            Book curBook = new Book();
            curBook.setName(name);
            int i = books.indexOf(curBook);
            curBook = books.get(i); // book that need be edited

            History.newItem("EditPage");

            setCurrentBook(curBook);
            RootPanel.get().add(new EditPage(this));
        });
    }

    /**
     * проверяем на валидность(существует ли книга, корректно ли все введено)
     * добавляем в массив книг
     * добавляем строчку в таблице
     */
    private void addRow() {
        final String name = this.name.getText().trim();
        final String author = this.author.getText().trim();
        int price;
        int count;

        if (!helper.validateString(name) || !helper.validateString(author)) {
            this.name.setFocus(true);
            return;
        }

        price = helper.validateNumber(this.price);
        if (price == -1) return;
        count = helper.validateNumber(this.count);
        if (count == -1) return;


        String priceDouble = NumberFormat.getFormat("#.00").format((double) price / 100);
        Book b = helper.checkBook(books, name, author, Double.parseDouble(priceDouble), count);

        if (b == null) {
            Window.alert("This book we have already");
            return;
        }

        int row = table.getRowCount();
        CheckBox checkBox = new CheckBox();
        table.setWidget(row, 0, checkBox);
        table.setText(row, 1, name);
        table.setText(row, 2, author);
        table.setText(row, 3, priceDouble);
        table.setText(row, 4, String.valueOf(count));
        table.setText(row, 5, helper.formatDate());

        helper.clear(this.name, this.author, this.price, this.count);
    }


    /**
     * Создаем таблицу
     */
    private void setupTable() {
        table = new DoubleClickTable();
        table.setText(0, 0, "");
        table.setText(0, 1, "Name");
        table.setText(0, 2, "Author");
        table.setText(0, 3, "Price");
        table.setText(0, 4, "Count");
        table.setText(0, 5, "Create date");

        table.setCellPadding(10);
    }


    Book getCurrentBook() {
        return currentBook;
    }

    private void setCurrentBook(Book currentBook) {
        this.currentBook = currentBook;
    }

    interface EntityUIBuilder extends UiBinder<VerticalPanel, EntityPage> {
    }

    private static EntityPage.EntityUIBuilder uiBinder = GWT.create(EntityPage.EntityUIBuilder.class);
}
