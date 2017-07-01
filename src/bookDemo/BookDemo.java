package bookDemo;

public class BookDemo{
    public static void main(String args[]){

        Book books[] =  new Book[2];
        books[0] = new Book("Java: The complete reference", "Schlidt", 2015);
        books[1] = new Book("The art of java", "Schlidt", 2012);

        for(int i =0; i < books.length; i++) books[i].show();
    }

}