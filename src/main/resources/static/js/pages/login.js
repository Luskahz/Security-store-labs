import {login} from '../api.js';
document.querySelector('#form').addEventListener('submit',async event=>{event.preventDefault();const error=document.querySelector('#error');error.textContent='';try{await login(document.querySelector('#email').value,document.querySelector('#password').value);location.href='/admin/index.html'}catch(e){error.textContent=e.message}});
