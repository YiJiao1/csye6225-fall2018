package csye6225Web.serviceController;

import csye6225Web.models.Receipt;
import csye6225Web.models.Transaction;
import csye6225Web.repositories.ReceiptRepository;
import csye6225Web.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
public class TransactionController {


    @Autowired
    private TransactionRepository transactionRepository;


    @Autowired
    private ReceiptRepository receiptRepository;

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {

        return transactionRepository.findAll();

    }


// in Asn2 get all transaction data funciton
    @GetMapping("/transaction/{id}")
    public ResponseEntity<Object> getTransaction(@PathVariable(value="id") Long id) {

       Optional<Transaction> transaction=transactionRepository.findById(id);
        if (!transaction.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id NOT FOUND\n");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        }

    }

// create a new transaction
    @PostMapping("/transaction")
    public ResponseEntity<Object> createNewTransaction(@RequestBody Transaction transaction) {

        try {

            for(Receipt r:transaction.getAttachments())
            {
                r.setTransaction(transaction);
            }

            transactionRepository.save(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);

        } catch (Exception e)
        {
            return ResponseEntity.badRequest().build();
        }

    }

// update transaction search by id
    @PutMapping("/transaction/{id}")
    public ResponseEntity<Object> updateTransaction(@RequestBody Transaction transaction ,@PathVariable Long id)
    {


        Optional<Transaction> old_transaction=transactionRepository.findById(id);

        if(!old_transaction.isPresent())
        {
            return ResponseEntity.notFound().build();
        }
        else
        {

            transaction.setId(id);
            for(Receipt r: transaction.getAttachments())
            {
                r.setTransaction(transaction);
            }

              transactionRepository.save(transaction);
              return ResponseEntity.status(HttpStatus.CREATED).body("Update Success!!\n");
        }
    }




// delete transaction by using id
    @DeleteMapping("transaction/{id}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable Long id)
    {

        Optional<Transaction> transaction=transactionRepository.findById(id);
        if (!transaction.isPresent())
        {
            return ResponseEntity.notFound().build();
        }
        else
        {
            for(Receipt r:transaction.get().getAttachments())
            {
                receiptRepository.delete(r);
            }
            transactionRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete_Success!!\n");
        }

    }




}
