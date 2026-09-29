import {api,clearTokens,tokens} from '../api.js';
import {escapeHtml} from '../auth.js';

const $=selector=>document.querySelector(selector);
const money=value=>new Intl.NumberFormat('pt-BR',{style:'currency',currency:'BRL'}).format(Number(value));
let me=null,products=[],selectedProduct=null;

function notify(text,error=false){const node=$('#notice');node.textContent=text;node.className=`notice ${error?'error':'success'}`;if(text)setTimeout(()=>{if(node.textContent===text)node.textContent=''},6000)}
function statusLabel(status){return ({PREPARING:'Preparando',SHIPPED:'Enviado',IN_TRANSIT:'Em trânsito',OUT_FOR_DELIVERY:'Saiu para entrega',DELIVERED:'Entregue',CANCELLED:'Cancelado'})[status]||status}
function nextDeliveryStatuses(status){return ({PREPARING:['SHIPPED','CANCELLED'],SHIPPED:['IN_TRANSIT','OUT_FOR_DELIVERY','DELIVERED'],IN_TRANSIT:['OUT_FOR_DELIVERY','DELIVERED'],OUT_FOR_DELIVERY:['IN_TRANSIT','DELIVERED'],DELIVERED:[],CANCELLED:[]})[status]||[]}
function can(permission){return me?.permissions?.includes(permission)}

async function loadCards(){
  if(!me)return;
  const cards=await api('/api/payment-cards');
  $('#saved-cards').innerHTML=cards.length?cards.map(card=>`<article class="saved-card"><div><strong>Cartão fictício</strong><small>•••• •••• •••• ${card.lastFour}</small></div><button class="remove-card secondary" data-token="${escapeHtml(card.token)}">Remover</button></article>`).join(''):'<p class="empty-state">Nenhum cartão cadastrado.</p>';
  document.querySelectorAll('.remove-card').forEach(button=>button.onclick=async()=>{try{await api(`/api/payment-cards/${encodeURIComponent(button.dataset.token)}`,{method:'DELETE'});notify('Cartão removido.');await loadCards()}catch(error){notify(error.message,true)}});
}

async function loadProfile(){
  const profile=await api('/identity/me/profile');
  const form=$('#profile-form');
  for(const [key,value] of Object.entries(profile))form.elements[key].value=value||'';
}

async function openPayment(orderId){
  try{const cards=await api('/api/payment-cards');if(!cards.length){notify('Cadastre um cartão fictício no seu perfil antes de pagar.',true);location.hash='payment-cards';return}
    $('#payment-order-id').value=orderId;
    $('#payment-card-options').innerHTML=cards.map((card,index)=>`<label><input type="radio" name="cardToken" value="${escapeHtml(card.token)}" ${index===0?'checked':''}><span>Cartão fictício<br><small>•••• •••• •••• ${card.lastFour}</small></span></label>`).join('');
    $('#payment-dialog').showModal();
  }catch(error){notify(error.message,true)}
}

async function loadProducts(){
  try{products=await api('/api/products',{method:'GET'});const admin=can('CATALOG_PRODUCT_WRITE');
    $('#products').innerHTML=products.length?products.map((p,index)=>`<article class="product-card"><div class="product-icon" aria-hidden="true">${['◈','⌘','▣','✳'][index%4]}</div><p class="eyebrow">ITEM DE LABORATÓRIO</p><h3>${escapeHtml(p.name)}</h3><p class="product-description">${escapeHtml(p.description||'Produto fictício do laboratório.')}</p><div class="product-meta"><strong>${money(p.price)}</strong><span>${p.stock>0?`${p.stock} disponíveis`:'Esgotado'}</span></div><div class="product-actions"><button class="button buy" data-id="${p.id}" ${p.stock<1||!me?'disabled':''}>${me?'Comprar':'Entre para comprar'}</button>${admin?`<button class="icon-button delete-product" data-id="${p.id}">Excluir</button>`:''}</div></article>`).join(''):'<p class="empty-state">Nenhum produto cadastrado ainda.</p>';
    document.querySelectorAll('.buy').forEach(button=>button.onclick=()=>openCheckout(products.find(p=>p.id===Number(button.dataset.id))));
    document.querySelectorAll('.delete-product').forEach(button=>button.onclick=async()=>{if(!confirm('Excluir este produto do catálogo?'))return;try{await api(`/api/products/${button.dataset.id}`,{method:'DELETE'});notify('Produto removido.');await loadProducts()}catch(error){notify(error.message,true)}});
  }catch(error){$('#products').innerHTML=`<p class="empty-state error">Não foi possível carregar o catálogo: ${escapeHtml(error.message)}</p>`}
}

function openCheckout(product){selectedProduct=product;$('#checkout-title').textContent=product.name;$('#checkout-price').textContent=`${money(product.price)} por unidade · ${product.stock} em estoque`;const quantity=$('#checkout-quantity');quantity.max=product.stock;quantity.value='1';updateTotal();$('#checkout-dialog').showModal()}
function updateTotal(){if(selectedProduct)$('#checkout-total').textContent=`Total: ${money(selectedProduct.price*Number($('#checkout-quantity').value||0))}`}

function renderOrders(orders){
  if(!orders.length){$('#orders-content').innerHTML='<article class="empty-state"><h3>Você ainda não tem pedidos</h3><p>Escolha um produto no catálogo para começar.</p><a class="button" href="#catalog">Ver produtos</a></article>';return}
  $('#orders-content').innerHTML=orders.map(order=>{const delivery=order.delivery||{};const address=order.shippingAddress||{};const canUpdate=can('LOGISTICS_DELIVERY_UPDATE');const next=nextDeliveryStatuses(delivery.status||'PREPARING');const shipping=[address.street,address.number,address.complement,address.neighborhood,address.city,address.state,address.postalCode].filter(Boolean).join(', ');return `<article class="order-card"><div class="order-top"><div><p class="eyebrow">PEDIDO #${order.id}</p><h3>${escapeHtml(order.product.name)} <span>× ${order.quantity}</span></h3></div><strong>${money(order.total)}</strong></div><div class="order-details"><span>Pagamento <b class="status">${order.paymentStatus==='PAID'?'Pago':order.paymentStatus==='PENDING'?'Pendente':'Reembolsado'}</b></span><span>Entrega <b class="status">${statusLabel(delivery.status||'PREPARING')}</b></span><span>Rastreio <b>${escapeHtml(delivery.trackingCode||'—')}</b></span><span>Previsão <b>${delivery.estimatedDeliveryAt?new Date(delivery.estimatedDeliveryAt).toLocaleDateString('pt-BR'):'—'}</b></span></div>${shipping?`<p class="order-shipping">Entrega para: ${escapeHtml(shipping)} · ${escapeHtml(address.phone||'')}</p>`:''}<div class="order-actions">${order.paymentStatus==='PENDING'?`<button class="button pay" data-id="${order.id}">Pagar (simulado)</button>`:''}${canUpdate&&next.length?`<label class="inline-label">Avançar entrega<select class="delivery-status" data-id="${order.id}">${next.map(s=>`<option value="${s}">${statusLabel(s)}</option>`).join('')}</select></label><button class="secondary update-delivery" data-id="${order.id}">Salvar status</button>`:''}</div></article>`}).join('');
  document.querySelectorAll('.pay').forEach(button=>button.onclick=()=>openPayment(button.dataset.id));
  document.querySelectorAll('.update-delivery').forEach(button=>button.onclick=async()=>{const status=document.querySelector(`.delivery-status[data-id="${button.dataset.id}"]`).value;button.disabled=true;try{await api(`/api/deliveries/orders/${button.dataset.id}`,{method:'PATCH',body:JSON.stringify({status})});notify('Entrega atualizada.');await loadOrders()}catch(error){notify(error.message,true);button.disabled=false}});
}

async function loadOrders(){if(!me)return;try{renderOrders(await api('/api/orders'))}catch(error){$('#orders-content').innerHTML=`<p class="empty-state error">Não foi possível carregar os pedidos: ${escapeHtml(error.message)}</p>`}}

async function loadAccount(){
  $('#account-name').textContent='Visitante';$('#login-link').hidden=false;$('#logout').hidden=true;$('#admin-link').hidden=true;$('#cards-link').hidden=true;$('#payment-cards').hidden=true;$('#refresh-orders').hidden=true;me=null;
  if(!tokens().access){$('#orders-content').innerHTML='<article class="empty-state"><h3>Acompanhe suas compras</h3><p>Entre ou crie uma conta para comprar e acompanhar as entregas.</p><a class="button" href="/login.html">Entrar na loja</a></article>';return}
  try{me=await api('/auth/me');$('#account-name').textContent=me.name;$('#login-link').hidden=true;$('#logout').hidden=false;$('#admin-link').hidden=me.permissions.length===0;$('#cards-link').hidden=false;$('#payment-cards').hidden=false;$('#profile-link').hidden=false;$('#profile').hidden=false;$('#refresh-orders').hidden=false;await loadOrders();await loadCards();await loadProfile()}
  catch(error){if(tokens().access){clearTokens();me=null;notify('Sua sessão expirou. Entre novamente.',true)}}
}

$('#checkout-quantity').addEventListener('input',updateTotal);
$('#new-card-number').addEventListener('input',event=>{const digits=event.target.value.replace(/\D/g,'').slice(0,19);event.target.value=digits.replace(/(.{4})/g,'$1 ').trim()});
$('#card-form').addEventListener('submit',async event=>{event.preventDefault();const field=$('#new-card-number');const digits=field.value.replace(/\D/g,'');if(digits.length<12||digits.length>19){notify('Informe um número fictício entre 12 e 19 dígitos.',true);return}const button=event.submitter;button.disabled=true;try{await api('/api/payment-cards',{method:'POST',body:JSON.stringify({cardNumber:digits})});field.value='';$('#card-dialog').close();notify('Cartão fictício salvo. O número completo foi descartado.');await loadCards()}catch(error){notify(error.message,true)}finally{button.disabled=false}});
$('#profile-form').addEventListener('submit',async event=>{event.preventDefault();const form=event.currentTarget;const data=Object.fromEntries(new FormData(form));const button=form.querySelector('button[type="submit"]');button.disabled=true;try{await api('/identity/me/profile',{method:'PUT',body:JSON.stringify(data)});$('#profile-message').textContent='Perfil e endereço salvos.';await loadProfile()}catch(error){$('#profile-message').textContent=error.message;$('#profile-message').className='notice error'}finally{button.disabled=false}});
$('#payment-form').addEventListener('submit',async event=>{event.preventDefault();const token=$('#payment-form input[name="cardToken"]:checked')?.value;if(!token)return;const button=$('#payment-submit');button.disabled=true;try{await api(`/api/orders/${$('#payment-order-id').value}/pay`,{method:'POST',body:JSON.stringify({cardToken:token})});$('#payment-dialog').close();notify('Pagamento fictício confirmado.');await loadOrders()}catch(error){notify(error.message,true)}finally{button.disabled=false}});
$('#checkout-form').addEventListener('submit',async event=>{event.preventDefault();if(!selectedProduct)return;const quantity=Number($('#checkout-quantity').value);if(!Number.isInteger(quantity)||quantity<1||quantity>selectedProduct.stock){notify('Quantidade fora do estoque disponível.',true);return}const button=$('#checkout-submit');button.disabled=true;try{await api('/api/orders',{method:'POST',body:JSON.stringify({productId:selectedProduct.id,quantity})});$('#checkout-dialog').close();notify('Pedido criado! Faça o pagamento simulado na seção Meus pedidos.');await loadProducts();await loadOrders();location.hash='orders'}catch(error){notify(error.message,true);if(error.message.includes('Complete seu telefone'))location.hash='profile'}finally{button.disabled=false}});
$('#refresh-products').onclick=loadProducts;$('#refresh-orders').onclick=loadOrders;
$('#logout').onclick=async()=>{try{await api('/auth/logout',{method:'POST'})}catch{}clearTokens();await loadAccount();await loadProducts();notify('Você saiu da sua conta.')};
document.querySelectorAll('.dialog-close').forEach(button=>button.onclick=()=>button.closest('dialog').close());$('#add-card-open').onclick=()=>$('#card-dialog').showModal();document.querySelectorAll('dialog').forEach(dialog=>dialog.addEventListener('click',event=>{if(event.target===dialog)dialog.close()}));

await loadAccount();await loadProducts();
