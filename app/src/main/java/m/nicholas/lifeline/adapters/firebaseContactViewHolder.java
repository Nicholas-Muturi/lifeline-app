package m.nicholas.lifeline.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Objects;

import m.nicholas.lifeline.Constants;
import m.nicholas.lifeline.R;
import m.nicholas.lifeline.models.Contact;
import m.nicholas.lifeline.ui.Fragment_Emergency_Contacts;

public class firebaseContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private View mView;

    public firebaseContactViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mView = itemView;
    }

    public void bindItems(Contact contact){
        TextView contactName = mView.findViewById(R.id.contactItemName);
        TextView contactNumber = mView.findViewById(R.id.contactItemNumber);
        ImageView delete = mView.findViewById(R.id.deleteItemIcon);

        contactName.setText(contact.getName());
        contactNumber.setText(contact.getNumber());
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        List<String> allContactKeys = new ArrayList<>();

        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_EMERGENCY_CONTACTS)
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    allContactKeys.add(snapshot.getKey());
                }
                String itemIdToRemove = allContactKeys.get(position);
                contactRef.child(itemIdToRemove).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
