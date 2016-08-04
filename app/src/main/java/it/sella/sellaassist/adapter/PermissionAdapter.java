package it.sella.sellaassist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.sella.sellaassist.R;
import it.sella.sellaassist.model.Permission;

/**
 * Created by GodwinRoseSamuel on 22-Jul-16.
 */
public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.PermissionViewHolder> {
    private static final String TAG = PermissionAdapter.class.getSimpleName();
    private List<Permission> permissions;

    public PermissionAdapter(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public PermissionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_permission, parent, false);
        return new PermissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PermissionViewHolder permissionViewHolder, int position) {
        permissionViewHolder.image.setBackgroundResource(permissions.get(position).getImageId());
        permissionViewHolder.title.setText(permissions.get(position).getTitle());
        permissionViewHolder.description.setText(permissions.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return permissions.size();
    }

    public class PermissionViewHolder extends RecyclerView.ViewHolder {
        public final ImageView image;
        public final TextView title;
        public final TextView description;

        public PermissionViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.list_permission_image);
            title = (TextView) view.findViewById(R.id.list_permission_title);
            description = (TextView) view.findViewById(R.id.list_permission_desc);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

