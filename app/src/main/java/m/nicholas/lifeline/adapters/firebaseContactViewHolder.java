package m.nicholas.lifeline.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import m.nicholas.lifeline.Constants;
import m.nicholas.lifeline.R;
import m.nicholas.lifeline.models.Contact;

public class firebaseContactViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

    private View mView;
    private Context mContext;

    public firebaseContactViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mContext = itemView.getContext();
        itemView.setOnLongClickListener(this);
    }

    public void bindItems(Contact contact){
        TextView contactName = mView.findViewById(R.id.contactItemName);
        TextView contactNumber = mView.findViewById(R.id.contactItemNumber);

        contactName.setText(contact.getName());
        contactNumber.setText(contact.getNumber());
    }

    @Override
    public boolean onLongClick(View view) {
        int position = getAdapterPosition();
        List<Contact> allContacts = new ArrayList<>();

        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_EMERGENCY_CONTACTS).child(FirebaseAuth.getInstance().getUid());
        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    allContacts.add(snapshot.getValue(Contact.class));
                }

                Toast.makeText(mContext,allContacts.get(position).getName(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return false;
    }
}
