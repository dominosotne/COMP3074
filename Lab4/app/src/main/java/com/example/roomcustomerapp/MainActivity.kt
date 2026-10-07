package com.example.roomcustomerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomcustomerapp.data.Customer
import com.example.roomcustomerapp.ui.theme.RoomCustomerAppTheme
import com.example.roomcustomerapp.viewmodel.CustomerViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoomCustomerAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    CustomerScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerScreen(
    modifier: Modifier = Modifier,
    customerViewModel: CustomerViewModel = viewModel()
) {

    val customers by customerViewModel.customers
        .collectAsStateWithLifecycle()

    var name by remember {
        mutableStateOf("")
    }

    var age by remember {
        mutableStateOf("")
    }

    var isActive by remember {
        mutableStateOf(true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Customer Database",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Name input
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("Name")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Age input
        OutlinedTextField(
            value = age,
            onValueChange = { newValue ->

                age = newValue.filter { character ->
                    character.isDigit()
                }
            },
            label = {
                Text("Age")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Active checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = isActive,
                onCheckedChange = {
                    isActive = it
                }
            )

            Text(
                text = "Active"
            )
        }

        // Add button
        Button(
            onClick = {

                val ageNumber = age.toIntOrNull()

                if (
                    name.isNotBlank() &&
                    ageNumber != null
                ) {

                    customerViewModel.addCustomer(
                        name = name.trim(),
                        age = ageNumber,
                        isActive = isActive
                    )

                    // Clear fields
                    name = ""
                    age = ""
                    isActive = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Add Customer"
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Customers",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Lazy list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(
                items = customers,
                key = { customer ->
                    customer.id
                }
            ) { customer ->

                CustomerItem(
                    customer = customer,
                    onDelete = {
                        customerViewModel.deleteCustomer(
                            customer
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun CustomerItem(
    customer: Customer,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = customer.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Age: ${customer.age}"
            )

            Text(
                text = if (customer.isActive) {
                    "Status: Active"
                } else {
                    "Status: Inactive"
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                Button(
                    onClick = onDelete
                ) {

                    Text(
                        text = "Delete"
                    )
                }
            }
        }
    }
}