module jmp.cloud.bank.impl {
  exports bank.impl;

  requires jmp.dto;
  requires transitive jmp.bank.api;

  provides bank.api.Bank with
      bank.impl.RetailBank,
      bank.impl.InvestmentBank,
      bank.impl.CentralBank;
}
