/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Class;


 public class Orders {
    private int id;
    private String orderDetails;
    private String customerName;
    private String email;

    public Orders(String orderDetails, String customerName, String email) {
        this.orderDetails = orderDetails;
        this.customerName = customerName;
        this.email = email;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

        public String getOrderDetails() {
            return orderDetails;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getEmail() {
            return email;
        }

    @Override
       public String toString() {
           return "Order ID: " + id + ", Details: " + orderDetails + ", Customer: " + customerName;
       }
    }
