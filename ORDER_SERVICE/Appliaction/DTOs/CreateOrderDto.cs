namespace OrderService.Application.DTOs;

public class CreateOrderDto
{
    public List<CreateOrderItemDto> Items { get; set; } = new();

    public string ShippingAddressJson { get; set; } = default!;
    public string BillingAddressJson { get; set; } = default!;
}
