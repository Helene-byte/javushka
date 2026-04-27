module jmp.cloud.service.impl {
  requires transitive jmp.service.api;
  requires jmp.dto;
  requires static lombok;
  requires jmp.cloud.bank.impl;

  exports service.impl;

  provides service.api.Service with
      service.impl.ServiceImpl;
}
