module jmp.application {
  requires jmp.cloud.bank.impl;
  requires jmp.dto;
  requires jmp.cloud.service.impl;

  uses bank.api.Bank;
  uses service.api.Service;
}
