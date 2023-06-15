package com.dam.juanmaseatitapp;

import static com.dam.juanmaseatitapp.Common.Common.convertCodeToStatus;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dam.juanmaseatitapp.Common.Common;
import com.dam.juanmaseatitapp.Model.Request;
import com.dam.juanmaseatitapp.Service.ListenOrder;
import com.dam.juanmaseatitapp.ViewHolder.OrderViewHolder;
import com.dam.juanmaseatitapp.databinding.FragmentOrderStatusBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Fragmento que muestra el estado de los pedidos realizados
 */
public class OrderStatusFragment extends Fragment {
    // Atributos de clase
    private FragmentOrderStatusBinding binding;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference requests;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderStatusBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // Inicializamos
        recyclerView = binding.listOrders;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Cargamos los pedidos
        loadOrders(Common.currentUser.getPhone());

        // TODO: Esto da error en la pestaña de pedidos, arreglar
        startService();

        return root;
    }

    /**
     * Este método carga la lista de pedidos
     */
    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Request model, int position) {
                orderViewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                orderViewHolder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                orderViewHolder.txtOrderAddress.setText(model.getAddress());
                orderViewHolder.txtOrderPhone.setText(model.getPhone());
            }
        };

        recyclerView.setAdapter(adapter);
    }

    private void startService() {
        // Registramos el servicio
        Intent service = new Intent(getActivity(), ListenOrder.class);
        getActivity().startService(service);
    }
}