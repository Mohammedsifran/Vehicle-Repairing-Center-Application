/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Class;

/**
 *
 * @author shafwananwer
 */
public class Supplier {
    private int id;
      private String supplierName;
        private String companyName;
        private String contact;

        public Supplier(String supplierName, String companyName, String contact) {
            this.supplierName = supplierName;
            this.companyName = companyName;
            this.contact = contact;
        }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

        public String getSupplierName() {
            return supplierName;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getContact() {
            return contact;
        }

        @Override
        public String toString() {
            return supplierName + " (Company: " + companyName + ", Contact: " + contact + ")";
        }
    }

