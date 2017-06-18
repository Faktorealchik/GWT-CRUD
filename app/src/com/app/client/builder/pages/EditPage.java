package com.app.client.builder.pages;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.app.client.builder.book.Book;
import com.app.client.builder.helper.Helper;

class EditPage extends Composite {
    interface EditUIPage extends UiBinder<VerticalPanel, EditPage> {
    }
    private static EditPage.EditUIPage uiBinder = GWT.create(EditPage.EditUIPage.class);


    @UiField
    TextBox name;
    @UiField
    TextBox author;
    @UiField
    TextBox price;
    @UiField
    TextBox count;

    @UiField
    Button changeButton;
    @UiField
    Button exitButton;
    @UiField
    Button updateDateButton;
    @UiField
    VerticalPanel verticalPanel;
    @UiField
    HTMLPanel htmlpanel;
    @UiField(provided = true)
    Grid gridTable;


    private Helper helper = new Helper();

    /**
     * Обновляет ячейку, на которую нажали
     * из
     *
     * @param entityPage мы вызываем текущую книгу(на нее мы нажали)
     *                   и затем мы обновляем ее и ячейку
     */
    EditPage(EntityPage entityPage) {
        setupTable();
        initWidget(uiBinder.createAndBindUi(this));
        entityPage.vertPanel.setVisible(false);
        entityPage.table.setVisible(false);

        Book currentBook = entityPage.getCurrentBook();
        gridTable.setText(1, 0, currentBook.getName());
        gridTable.setText(1, 1, currentBook.getAuthor());
        gridTable.setText(1, 2, String.valueOf(currentBook.getPrice()));
        gridTable.setText(1, 3, String.valueOf(currentBook.getCount()));
        gridTable.setWidget(1, 4, updateDateButton);

        name.setText(currentBook.getName());
        author.setText(currentBook.getAuthor());
        price.setText(String.valueOf(currentBook.getPrice() * 100));
        count.setText(String.valueOf(currentBook.getCount()));


        int i = entityPage.books.indexOf(currentBook);

        changeButton.addClickHandler(event -> {
            String textName = this.name.getText().trim();
            String author = this.author.getText().trim();
            int price;
            int count;

            if (!helper.validateString(textName) ||
                    !helper.validateString(author)) {
                this.name.setFocus(true);
                return;
            }

            price = helper.validateNumber(this.price);
            if (price == -1) return;
            count = helper.validateNumber(this.count);
            if (count == -1) return;

            String priceDouble = NumberFormat.getFormat("#.00").format((double) price / 100);
            Book b = new Book(textName, author, Double.parseDouble(priceDouble), count);


            //если у нас книга не поменялась, или эта книга берет название чужой книги,
            //то ошибка - эта книга у нас уже имеется.
            if (entityPage.books.contains(b)) {
                int id = entityPage.books.indexOf(b);
                String tmpAuthor = entityPage.books.get(id).getAuthor();
                double tmpPrice = entityPage.books.get(id).getPrice();
                int tmpCount = entityPage.books.get(id).getCount();
                if (tmpAuthor.equals(author) &&
                        tmpPrice == Double.parseDouble(priceDouble) &&
                        count == tmpCount ||
                        i != id) {
                    Window.alert("This book we have already");
                    return;
                }
            }

            entityPage.books.set(i, b);
            entityPage.table.setText(i + 1, 1, textName);
            entityPage.table.setText(i + 1, 2, author);
            entityPage.table.setText(i + 1, 3, priceDouble);
            entityPage.table.setText(i + 1, 4, String.valueOf(count));

            exit(entityPage);
        });

        exitButton.addClickHandler(event -> {
            //нужно установить новую книгу, чтобы для нее создавался новый класс EditPage
            //иначе просто нельзя будет опять редактировать эту книгу
            entityPage.books.set(entityPage.books.indexOf(currentBook), currentBook);
            exit(entityPage);
        });

        updateDateButton.addClickHandler(event -> {
            entityPage.table.setText(i + 1, 5, helper.formatDate());
            exit(entityPage);
        });
    }

    /**
     * при выходе восстановить прошлые данные, удалить этот класс из RootPanel
     */
    private void exit(EntityPage entityPage) {
        changeButton.setVisible(false);
        entityPage.vertPanel.setVisible(true);
        entityPage.table.setVisible(true);
        helper.clear(this.name, this.author, this.price, this.count);
        RootPanel.get().remove(this);
        History.back();
    }

    private void setupTable() {
        gridTable = new Grid(2, 5);
        // создает таблицу
        gridTable.setText(0, 0, "Name");
        gridTable.setText(0, 1, "Author");
        gridTable.setText(0, 2, "Price");
        gridTable.setText(0, 3, "Count");
        gridTable.setText(0, 4, "Update date");

        gridTable.setCellPadding(10);
    }
}
