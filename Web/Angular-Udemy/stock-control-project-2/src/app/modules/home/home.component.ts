import { Component } from '@angular/core';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button'; // ✅ Importação correta
import { ToastModule } from 'primeng/toast'; // ✅ Importação correta
import { MessageService } from 'primeng/api'; // ✅ Serviço necessário para Toast
import { CommonModule } from '@angular/common';
import { FormGroup, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CardModule,
    InputTextModule,
    ButtonModule,
    ToastModule,
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  providers: [MessageService] // ✅ Necessário para usar o Toast
})
export class HomeComponent {
  loginCard = true;

  loginForm!: FormGroup;
  signupform!: FormGroup;

  constructor(
    private messageService: MessageService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      email: ['', Validators.required],
      password: ['', Validators.required],
    });


    this.signupform = this.formBuilder.group({
      name: ['', Validators.required],
      email: ['', Validators.required],
      password: ['', Validators.required],
    });
  }


  onSubmitForm(): void {
    console.log('Dados do formulário de LOGIN', this.loginForm.value)
  }

  onSingForm(): void {
    console.log('Dados do formulário de CRIAÇÃO', this.signupform.value)
  }

  showToast() {
    this.messageService.add({
      severity: 'success',
      summary: 'Sucesso!',
      detail: 'Login realizado com sucesso!',
      life: 3000 // Tempo de exibição em milissegundos
    });
  }

}
