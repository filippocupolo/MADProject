package com.example.andrea.lab11;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class recyclerView_adapter extends RecyclerView.Adapter<recyclerView_adapter.BookViewHolder> {

   private List<BookInfo> books;

    recyclerView_adapter(List<BookInfo> books)
    {
        this.books = books;
    }

    public int getItemCount()
    {
        return books.size();
    }



    public static class BookViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView bookTitle;
        TextView bookAuthor;
        TextView bookISBN;
        TextView bookPublicationYear;
        ImageView bookPhoto;

        BookViewHolder(View itemView){

            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cardViewBook);
            bookTitle = (TextView)itemView.findViewById(R.id.titleResult);
            bookAuthor = (TextView)itemView.findViewById(R.id.authorResult);
            bookISBN = (TextView)itemView.findViewById(R.id.ISBNresult);
            bookPublicationYear = (TextView)itemView.findViewById(R.id.yearResult);
            bookPhoto = (ImageView)itemView.findViewById(R.id.imageResult);
        }

    }

    public BookViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_search_results_list, viewGroup, false);
        BookViewHolder bvh = new BookViewHolder(v);
        return bvh;
    }

    public void onBindViewHolder(BookViewHolder bookViewHolder, int i) {
        bookViewHolder.bookTitle.setText(books.get(i).getBookTitle());
        bookViewHolder.bookAuthor.setText(books.get(i).getAuthor());
        bookViewHolder.bookISBN.setText(books.get(i).get_ISBN());
        bookViewHolder.bookPublicationYear.setText(books.get(i).getEditionYear());
        bookViewHolder.bookPhoto.setImageBitmap(books.get(i).getFirstPhoto());
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
