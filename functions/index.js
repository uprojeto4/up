const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNewMessageNotification = functions.database.ref('/negotiations/{user_id}/{ad_id}/lastMessageSenderId').onWrite(event => {
	
	// obtem o id do usuario e do anuncio onde ocorreram mudancas na negociacao
    const user_id = event.params.user_id;
	const ad_id = event.params.ad_id;
	const lastMessageSenderId = event.params.lastMessageSenderId;
	console.log('Trying to notify user ', user_id);
	console.log('lastMessageSenderId: ' + lastMessageSenderId);
    
    if (event.data.val() || lastMessageSenderId != user_id) {
		// obtem demais dados da negociacao
		const negotiationMetadata = admin.database().ref(`/negotiations/${user_id}/${ad_id}`).once('value');
	
		return negotiationMetadata.then(negotiationResult => {
			
			// obtem demais dados da negociacao
			const lastMessage = negotiationResult.val().lastMessage;
			const remoteUserId = negotiationResult.val().remoteUserId;
			const vendorId = negotiationResult.val().vendorId;
			let negotiationType = 0;
	
			if (user_id != vendorId) {
				negotiationType = 1;
			}
	
			// obtem o titulo da negociacao
			const adTitleQuery = admin.database().ref(`/posts/${ad_id}/title`).once('value');
	
			return adTitleQuery.then(adTitleQueryResult => {
	
				const adTitle = adTitleQueryResult.val();
	
				// obtem o nome do usuario que enviou a mensagem
				const messageSenderQuery = admin.database().ref(`/users/${lastMessageSenderId}/nome`).once('value');
				
				return messageSenderQuery.then(userResult => {
					
					const userName  = userResult.val();
					
					console.log(userName);
	
					// obtem o token do dispositivo associado ao usuario
					const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');
		
					return deviceToken.then(result => {
		
						const token_id  = result.val();
						const payload = {
							notification: {
								title: `Nova mensagem em ${adTitle}`,
								body: `${userName}: ${lastMessage}`,
								icon: "default",
								click_action: "br.ufc.quixada.up_TARGET_NOTIFICATION"
							},
							data: {
								adId: ad_id,
								remoteUserId: remoteUserId,
								negotiationType: negotiationType.toString()
							}
						};
					
						return admin.messaging().sendToDevice(token_id, payload).then(response => {
							console.log("Notification sent to ", user_id);
						});
		
					});
				
				});
	
			});
			
		});
	}

});
