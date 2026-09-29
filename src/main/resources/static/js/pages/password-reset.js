const token=new URLSearchParams(location.hash.slice(1)).get('token');
const requestView=document.querySelector('#request-view'),resetView=document.querySelector('#reset-view'),message=document.querySelector('#reset-message');
if(token){requestView.hidden=true;resetView.hidden=false;history.replaceState(null,'',location.pathname)}
document.querySelector('#recovery-form').addEventListener('submit',async event=>{
  event.preventDefault();const button=event.submitter;button.disabled=true;message.textContent='';
  try{const response=await fetch('/auth/password-recovery',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({email:document.querySelector('#recovery-email').value})});if(!response.ok)throw new Error('Não foi possível processar agora. Tente novamente mais tarde.');const result=await response.json();message.textContent=result.message}
  catch(error){message.textContent=error.message;message.className='notice error'}finally{button.disabled=false}
});
document.querySelector('#reset-form').addEventListener('submit',async event=>{
  event.preventDefault();const button=event.submitter;button.disabled=true;message.textContent='';
  try{const response=await fetch('/auth/password-reset',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({token,newPassword:document.querySelector('#new-password').value})});if(!response.ok)throw new Error('Link inválido ou expirado. Solicite uma nova recuperação.');history.replaceState(null,'',location.pathname);resetView.hidden=true;message.textContent='Senha redefinida. Você já pode entrar.'}
  catch(error){message.textContent=error.message;message.className='notice error'}finally{button.disabled=false}
});
