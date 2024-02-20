/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Class;

public class Allocation {
    private int id;
    private String employeeName;
    private int orderId;

    public Allocation(String employeeName, int orderId) {
        this.employeeName = employeeName;
        this.orderId = orderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }


    @Override
    public String toString() {
        return "Allocation{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", orderId=" + orderId +
                '}';
    }
}
