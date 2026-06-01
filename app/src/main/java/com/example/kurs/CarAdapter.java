package com.example.kurs;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private final Context context;
    private final ArrayList<Car> carList;
    private final OnCarClickListener listener;
    private final boolean isProfileMode;
    private final OnDeliveryConfirmListener deliveryListener;

    public interface OnCarClickListener {
        void onCarClick(Car car);
    }

    public interface OnDeliveryConfirmListener {
        void onConfirm(Car car);
    }

    public CarAdapter(Context context, ArrayList<Car> carList, OnCarClickListener listener) {
        this(context, carList, listener, false, null);
    }

    public CarAdapter(Context context, ArrayList<Car> carList, OnCarClickListener listener, boolean isProfileMode, OnDeliveryConfirmListener deliveryListener) {
        this.context = context;
        this.carList = carList;
        this.listener = listener;
        this.isProfileMode = isProfileMode;
        this.deliveryListener = deliveryListener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);

        holder.txtTitle.setText(car.getTitle());
        holder.txtDesc.setText(car.getDescription());

        if (car.getImageUri() != null && !car.getImageUri().isEmpty()) {
            holder.imgCar.setImageURI(Uri.parse(car.getImageUri()));
        } else {
            holder.imgCar.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        if (car.getPrice() == 0) {
            holder.txtPrice.setText("Бесплатно");
        } else {
            holder.txtPrice.setText(String.format(Locale.getDefault(), "%,d руб.", car.getPrice()));
        }

        // Логика доставки (только в профиле для чужих машин)
        if (isProfileMode && !car.getAuthor().equals("Vlad")) {
            holder.layoutDelivery.setVisibility(View.VISIBLE);
            holder.btnConfirmDelivery.setOnClickListener(v -> {
                if (deliveryListener != null) deliveryListener.onConfirm(car);
            });
        } else {
            holder.layoutDelivery.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCarClick(car);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCar;
        TextView txtTitle, txtDesc, txtPrice;
        LinearLayout layoutDelivery;
        Button btnConfirmDelivery;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCar = itemView.findViewById(R.id.imgCar);
            txtTitle = itemView.findViewById(R.id.txtCarTitle);
            txtDesc = itemView.findViewById(R.id.txtCarDesc);
            txtPrice = itemView.findViewById(R.id.txtCarPrice);
            layoutDelivery = itemView.findViewById(R.id.layoutDelivery);
            btnConfirmDelivery = itemView.findViewById(R.id.btnConfirmDelivery);
        }
    }
}