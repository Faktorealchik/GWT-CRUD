package com.app.client.builder.helper;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextBox;
import com.app.client.builder.book.Book;

import java.util.Date;
import java.util.List;

public class Helper {

    /**
     * validateString String
     *
     * @param st строка, которая будет проверена на валидность
     */
    public boolean validateString(String st) {
        if (!st.matches("^[0-9A-Za-zА-Яа-я\\.\\s]{1,30}$")) {
            Window.alert("This name/author incorrect '" + st + "' is not valid (1-30 symbols).");
            return false;
        }
        return true;
    }

    /**
     * validateNumber из textBox (count, price)
     * возвращает значение указанное в textBox`e
     * */
    public int validateNumber(TextBox box){
        try {
            return Integer.parseInt(box.getText());
        } catch (NumberFormatException e) {
            Window.alert("Not a number.");
            box.setFocus(true);
            return -1;
        }
    }

    /**
     * очищаем texBox`ы
     */
    public void clear(TextBox name, TextBox author, TextBox price, TextBox count) {
        name.setText("");
        author.setText("");
        price.setText("");
        count.setText("");
        name.setFocus(true);
    }

    /**
     * Проверяем существует ли книга
     * если да-возвращаем null
     * нет- добавляем книгу в список книг, возвращаем ее
     */
    public Book checkBook(List<Book> books, String name, String author, double price, int count) {
        Book book = new Book();
        book.setName(name);
        book.setAuthor(author);
        book.setPrice(price);
        book.setCount(count);

        if (books.contains(book)) {
            return null;
        } else {
            books.add(book);
            return book;
        }
    }


    /**
     * Возвращает новую дату
     * */
    public String formatDate(){
        DateTimeFormat dateFormat = DateTimeFormat.getFormat(
                DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        return dateFormat.format(new Date());
    }

}
