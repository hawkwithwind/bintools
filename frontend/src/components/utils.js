import axios from 'axios';

export async function downloadStaticFile(fileName) {
    let response = await axios({
        method: 'GET',
        url: '/static/' + fileName,
        responseType: 'blob',
    });

    return await response.data.text();
}